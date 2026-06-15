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
        android.util.Log.d("MyLog", "ViewModel: Запрос начат...")
        try {
            val response = newsRepository.getTopHeadlines(countryCode, 1, apiKey)
            topHeadlines.postValue(response)
            android.util.Log.d("MyLog", "ViewModel: Ответ получен!")
        } catch (e: Exception) {
            android.util.Log.e("MyLog", "ViewModel: Ошибка: ${e.message}")
        }
    }

}