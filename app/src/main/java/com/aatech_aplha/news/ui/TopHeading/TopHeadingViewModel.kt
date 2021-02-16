package com.aatech_aplha.news.ui.TopHeading

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.Database.NewsSavedDao
import com.aatech_aplha.news.data.ConnectionLiveData
import com.aatech_aplha.news.data.NewsRepository
import com.aatech_aplha.news.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TopHeadingViewModel @ViewModelInject constructor(
    private val newsRepository: NewsRepository,
    private val connectionLiveData: ConnectionLiveData,
    private val newsSavedDao: NewsSavedDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    val news = newsRepository.getNews().cachedIn(viewModelScope)

    val isConnected = connectionLiveData

    private val newsEventChannel = Channel<NewsEvent>()

    val newsEventFlow = newsEventChannel.receiveAsFlow()

    fun addNews(newsSaved: NewsSaved) = applicationScope.launch(Dispatchers.IO) {
        try {
            newsSavedDao.addNews(newsSaved)
            newsEventChannel.send(NewsEvent.NewsAdded)
        } catch (e: Exception) {
            newsEventChannel.send(NewsEvent.NewsNotAdded)
        }
    }

    sealed class NewsEvent {
        object NewsAdded : NewsEvent()
        object NewsNotAdded : NewsEvent()
    }
}