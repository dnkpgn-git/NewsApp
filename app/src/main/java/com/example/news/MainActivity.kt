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

    }

    private fun setupRecyclerView(){
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}