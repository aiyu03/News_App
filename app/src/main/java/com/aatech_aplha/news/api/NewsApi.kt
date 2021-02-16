package com.aatech_aplha.news.api

import com.aatech_aplha.news.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val CLIENT_ID = BuildConfig.NEWS_ACCESS_KEY
    }

    @Headers("Accept-Version: v2", "Authorization:Client-ID $CLIENT_ID")
    @GET("top-headlines")
    suspend fun topHeading(
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsApiResponse

    @Headers("Accept-Version: v2", "Authorization:Client-Id $CLIENT_ID")
    @GET("everything")
    suspend fun everyThing(
        @Query("q") q: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsApiResponse
}