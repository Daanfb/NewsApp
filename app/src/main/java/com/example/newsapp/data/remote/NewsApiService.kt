package com.example.newsapp.data.remote

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.remote.response.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String = "general",
        @Query("pageSize") pageSize: Int = Constants.PAGE_SIZE,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY,
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = Constants.PAGE_SIZE,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY,
    ): Response<NewsResponse>
}