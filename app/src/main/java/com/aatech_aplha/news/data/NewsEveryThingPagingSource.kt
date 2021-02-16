package com.aatech_aplha.news.data

import androidx.paging.PagingSource
import com.aatech_aplha.news.api.NewsApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val NEWS_API_STARTING_PAGE = 1

class NewsEveryThingPagingSource @Inject constructor(
    private val newsApi: NewsApi,
    private val query: String
) : PagingSource<Int, News>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        val position = params.key ?: NEWS_API_STARTING_PAGE
        return try {
            val response = newsApi.everyThing(
                q = query,
                language = "en",
                sortBy = "publishedAt",
                page = position,
                pageSize = params.loadSize
            )
            val news = response.articles
            LoadResult.Page(
                data = news,
                prevKey = if (position == NEWS_API_STARTING_PAGE) null else position - 1,
                nextKey = if (news.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}