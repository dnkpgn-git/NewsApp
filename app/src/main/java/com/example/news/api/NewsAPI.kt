package com.example.news.api

import com.example.news.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getTopHeadLines(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String
    ): Response<NewsResponse>
}