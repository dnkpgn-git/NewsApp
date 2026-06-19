package com.example.news

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.news.databinding.ActivityMainBinding
import com.example.news.databinding.SearchNewsArticleBinding

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: SearchNewsArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchNewsArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleUrl = intent.getStringExtra("article_url")

        binding.webView.apply {

            webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.pbWebView.visibility = View.GONE
                }
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36"
                loadWithOverviewMode = true
                javaScriptCanOpenWindowsAutomatically = true
            }

            if (articleUrl != null){
                loadUrl(articleUrl)
            }

        }
    }

}