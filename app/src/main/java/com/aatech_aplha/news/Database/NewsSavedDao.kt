package com.aatech_aplha.news.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsSavedDao {

    @Insert()
    suspend fun addNews(newsSaved: NewsSaved)

    @Delete
    suspend fun deleteNews(newsSaved: NewsSaved)

    @Query("SELECT * FROM news_saved ORDER BY created ASC")
    fun getSavedNews(): LiveData<List<NewsSaved>>
}