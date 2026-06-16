package com.example.news

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.ActivityMainBinding
import com.example.news.repository.NewsRepository
import com.example.news.ui.NewsViewModel
import com.example.news.ui.NewsViewModelProviderFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
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

        setupRecyclerView()

        //"Горячие" новости
        viewModel.topHeadlines.observe(this){response ->
            android.util.Log.d("MyLog", "Response status: ${response.code()}")
            if(response.isSuccessful){
                response.body()?.let { newsResponse ->
                    newsAdapter = NewsAdapter(newsResponse.articles)
                    binding.rvArticle.adapter = newsAdapter
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
                job = MainScope().launch {
                    kotlinx.coroutines.delay(500L)
                    newText?.let {
                        if (it.isNotEmpty()){
                            viewModel.searchNews(it, BuildConfig.API_KEY)
                        } else{
                            viewModel.topHeadlines.value?.body()?.let { newsResponse ->
                                newsAdapter = NewsAdapter(newsResponse.articles)
                                binding.rvArticle.adapter = newsAdapter
                            }
                        }
                    }
                }
                return true
            }
        })

        viewModel.searchNews.observe(this){response ->
            response?.body()?.let { newsResponse ->
                newsAdapter = NewsAdapter(newsResponse.articles)
                binding.rvArticle.adapter = newsAdapter
            }
        }

    }

    private fun setupRecyclerView(){
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}