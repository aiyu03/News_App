package com.aatech_aplha.news.api

import com.aatech_aplha.news.data.News

data class NewsApiResponse(
    val articles: List<News>
)