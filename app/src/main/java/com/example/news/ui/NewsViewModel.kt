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
    var topHeadlinesPage = 1
    var topHeadlinesResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Response<NewsResponse>?> = MutableLiveData()

    var isError = false
    var isLoading = false

    fun getTopHeadlines(countryCode: String, apiKey: String) = viewModelScope.launch {
        android.util.Log.d("MyLog", "ViewModel getTopHeadlines(): Запрос начат...")
        if (isLoading) return@launch
        isLoading = true
        try {
            val response = newsRepository.getTopHeadlines(countryCode, 1, apiKey)
            if (response.isSuccessful){
                response.body()?.let { resultResopnse ->
                    topHeadlinesPage++
                    if (topHeadlinesResponse == null){
                        topHeadlinesResponse = resultResopnse
                    } else {
                        val oldArticles = topHeadlinesResponse?.articles
                        val newArticles = resultResopnse.articles
                        topHeadlinesResponse?.articles?.addAll(newArticles)
                    }
                }
            }
            topHeadlines.postValue(Response.success(topHeadlinesResponse))

            android.util.Log.d("MyLog", "ViewModel: Ответ получен!")
        } catch (e: Exception) {
            android.util.Log.e("MyLog", "ViewModel: Ошибка: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    fun searchNews(searchQuery: String, apiKey: String) = viewModelScope.launch {
        searchNews.postValue(null)
        try {
            val response = newsRepository.searchForNews(searchQuery, 1, apiKey)
            searchNews.postValue(response)
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }

}