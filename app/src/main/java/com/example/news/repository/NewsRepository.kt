package com.example.news.repository

import com.example.news.api.RetrofitInstance
import java.util.Locale

class NewsRepository {
    suspend fun getTopHeadlines(countryCode: String, pageNumber: Int, apiKey: String) =
        RetrofitInstance.api.getTopHeadLines(countryCode, pageNumber, apiKey)
}