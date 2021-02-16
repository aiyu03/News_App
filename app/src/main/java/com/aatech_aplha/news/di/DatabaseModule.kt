package com.aatech_aplha.news.di

import android.content.Context
import androidx.room.Room
import com.aatech_aplha.news.Database.NewsSavedDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {


    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, NewsSavedDatabase::class.java, "NewsSaved")
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    @Synchronized
    fun provideDao(
        newsSavedDatabase: NewsSavedDatabase
    ) = newsSavedDatabase.getNewsSavedDao()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope