package com.example.news.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.models.NewsResponse
import com.example.news.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {

    val topHeadlines: MutableLiveData<Response<NewsResponse>> = MutableLiveData()

    fun getTopHeadlines(countryCode: String, apiKey: String) = viewModelScope.launch {
        val response = newsRepository.getTopHeadlines(countryCode, 1, apiKey)
        topHeadlines.postValue(response)
    }

}