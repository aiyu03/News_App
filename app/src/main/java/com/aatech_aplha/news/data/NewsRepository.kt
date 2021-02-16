package com.aatech_aplha.news.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.aatech_aplha.news.api.NewsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val newsApi: NewsApi) {

    fun getNews() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 80,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource(newsApi) }
        ).liveData

    fun getSearch(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 80,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsEveryThingPagingSource(newsApi, query) }
        ).liveData
}