package com.aatech_aplha.news.data

import java.io.Serializable


data class News(
    val source: NewsSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
) : Serializable {
    data class NewsSource(
        val id: String?,
        val name: String
    ) : Serializable
}