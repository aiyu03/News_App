package com.aatech_aplha.news.ui.SavedNews

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.Database.NewsSavedDao
import com.aatech_aplha.news.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SavedFragmentViewModel @ViewModelInject constructor(
    private val newsSavedDao: NewsSavedDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    val savedNews = newsSavedDao.getSavedNews()
    private val savedEventChannel = Channel<SavedEvent>()

    val savedEvent = savedEventChannel.receiveAsFlow()

    fun deleteNews(newsSaved: NewsSaved) = applicationScope.launch(Dispatchers.IO) {
        newsSavedDao.deleteNews(newsSaved)
        savedEventChannel.send(SavedEvent.NewsDeleted)
    }

    sealed class SavedEvent {
        object NewsDeleted : SavedEvent()
    }
}