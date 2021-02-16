package com.aatech_aplha.news.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsSaved::class], version = 1)
abstract class NewsSavedDatabase : RoomDatabase() {

    abstract fun getNewsSavedDao(): NewsSavedDao
}