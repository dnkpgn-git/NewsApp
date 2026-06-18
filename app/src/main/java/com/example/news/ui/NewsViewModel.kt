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
    var isTopHeadlinesLastPage = false
    val searchNews: MutableLiveData<Response<NewsResponse>?> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var lastSearchQuery: String? = null
    var isSearchLastPage = false

    var isLoading = false

    companion object {
        const val QUERY_PAGE_SIZE = 20
    }

    fun getTopHeadlines(countryCode: String, apiKey: String) = viewModelScope.launch {
        if (isLoading || isTopHeadlinesLastPage) return@launch
        android.util.Log.d("MyLog", "ViewModel getTopHeadlines(): Запрос начат...")
        isLoading = true
        try {
            val response = newsRepository.getTopHeadlines(countryCode, topHeadlinesPage, apiKey)
            if (response.isSuccessful){
                response.body()?.let { resultResopnse ->
                    topHeadlinesPage++
                    if (topHeadlinesResponse == null){
                        topHeadlinesResponse = resultResopnse
                    } else {
                        val newArticles = resultResopnse.articles
                        topHeadlinesResponse?.articles?.addAll(newArticles)
                    }
                    topHeadlines.postValue(Response.success(topHeadlinesResponse))

                    val totalPages = (resultResopnse.totalResults / QUERY_PAGE_SIZE) + 1
                    isTopHeadlinesLastPage = topHeadlinesPage > totalPages
                }
            } else {
                topHeadlines.postValue(response)
            }
            android.util.Log.d("MyLog", "ViewModel: Ответ получен!")
        } catch (e: Exception) {
            android.util.Log.e("MyLog", "ViewModel: Ошибка: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    fun searchNews(searchQuery: String, apiKey: String) = viewModelScope.launch {
        if (searchQuery != lastSearchQuery){
            searchNewsPage = 1
            searchNewsResponse = null
            lastSearchQuery = searchQuery
            isSearchLastPage = false
            searchNews.postValue(null)
        }
        if (isLoading || isSearchLastPage) return@launch
        isLoading = true

        try {
            val response = newsRepository.searchForNews(searchQuery, searchNewsPage, apiKey)
            if (response.isSuccessful){
                response.body()?.let { resultResponse ->
                    searchNewsPage++
                    if (searchNewsResponse == null){
                        searchNewsResponse = resultResponse
                    } else {
                        val newArticles = resultResponse.articles
                        searchNewsResponse?.articles?.addAll(newArticles)
                    }
                    searchNews.postValue(Response.success(searchNewsResponse ?: resultResponse))
                    val totalPages = (resultResponse.totalResults / QUERY_PAGE_SIZE) + 1
                    isSearchLastPage = searchNewsPage > totalPages
                }
            } else {
                searchNews.postValue(response)
            }
        } catch (e: Exception) {
            android.util.Log.e("MyLog", "Search Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

}