package com.aatech_aplha.news.ui.AllNews

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AllNewsViewModel @ViewModelInject constructor(
    private val repository: NewsRepository,
    private val connectionLiveData: ConnectionLiveData,
    private val newsSavedDao: NewsSavedDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    val searchQuery = MutableStateFlow(DEFAULT_QUERY)

    val isConnected = connectionLiveData

    private val newsEventChannel = Channel<NewsEvent>()

    val newsEventFlow = newsEventChannel.receiveAsFlow()

    private val newsFlow = searchQuery.flatMapLatest {
        repository.getSearch(it).cachedIn(viewModelScope).asFlow()
    }
    val news = newsFlow.asLiveData()

    fun addNews(newsSaved: NewsSaved) = applicationScope.launch(Dispatchers.IO) {
        try {
            newsSavedDao.addNews(newsSaved)
            newsEventChannel.send(NewsEvent.NewsAdded)
        } catch (e: Exception) {
            newsEventChannel.send(NewsEvent.NewsNotAdded)
        }
    }

    companion object {
        const val DEFAULT_QUERY = "Technology"
    }

    sealed class NewsEvent {
        object NewsAdded : NewsEvent()
        object NewsNotAdded : NewsEvent()
    }
}