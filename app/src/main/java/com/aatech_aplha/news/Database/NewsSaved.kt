package com.aatech_aplha.news.Database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "news_saved")
data class NewsSaved(
    val author: String?,
    @PrimaryKey(autoGenerate = false)
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    @Embedded
    val source: NewsSource,
    val created: Long = System.currentTimeMillis()
) {
    data class NewsSource(
        val id: String?,
        val name: String
    ) : Serializable
}
