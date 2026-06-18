package com.example.news

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.ActivityMainBinding
import com.example.news.repository.NewsRepository
import com.example.news.ui.NewsViewModel
import com.example.news.ui.NewsViewModelProviderFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = NewsRepository()
        val factory = NewsViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        newsAdapter = NewsAdapter()
        setupRecyclerView()

        //"Горячие" новости
        viewModel.topHeadlines.observe(this){response ->
            android.util.Log.d("MyLog", "Response status: ${response.code()}")
            if(response.isSuccessful){
                response.body()?.let { newsResponse ->
                    newsAdapter.differ.submitList(newsResponse.articles.toList())
                }
            }
            else {
                android.util.Log.e("MyLog", "Error: ${response.errorBody()?.string()}")
            }
        }

        viewModel.getTopHeadlines("us", BuildConfig.API_KEY)

        //Слушатель поля поиска
        var job: Job? = null
        binding.svSearchNews.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(500L)
                    newText?.let {
                        if (it.isNotEmpty()){
                            viewModel.searchNews(it, BuildConfig.API_KEY)
                        } else{
                            viewModel.topHeadlines.value?.body()?.let { newsResponse ->
                                newsAdapter.differ.submitList(newsResponse.articles.toList())
                            }
                        }
                    }
                }
                return true
            }
        })
        viewModel.searchNews.observe(this){response ->
            if (response != null) {
                if (response.isSuccessful) {
                    response?.body()?.let { newsResponse ->
                        android.util.Log.d("MyLog", "Найдено статей: ${newsResponse.articles.size} из ${newsResponse.totalResults}")
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                } else {
                    android.util.Log.e("MyLog", "Search Error: ${response.errorBody()?.string()}")
                }
            }

        }

    }

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= visibleItemCount

            val query = binding.svSearchNews.query.toString()
            val isLastPage = if (query.isEmpty()) viewModel.isTopHeadlinesLastPage else viewModel.isSearchLastPage

            val shouldPaginate = isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && !viewModel.isLoading && !isLastPage

            if(shouldPaginate) {
                val query = binding.svSearchNews.query.toString() // Достаем текущий текст поиска

                if (query.isEmpty()) {
                    android.util.Log.d("PaginationLog", "Пагинация ГЛАВНОЙ, стр: ${viewModel.topHeadlinesPage}")
                    viewModel.getTopHeadlines("us", BuildConfig.API_KEY)
                } else {
                    android.util.Log.d("PaginationLog", "Пагинация ПОИСКА, стр: ${viewModel.searchNewsPage}")
                    viewModel.searchNews(query, BuildConfig.API_KEY)
                }
            }
        }
    }

    private fun setupRecyclerView(){
        binding.rvArticle.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(scrollListener)
        }
    }
}