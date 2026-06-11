package com.example.news.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.news.databinding.ItemArticleBinding
import com.example.news.models.Article
import com.bumptech.glide.Glide

class NewsAdapter(private val articleList: List<Article>): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(inflater, parent, false)
        return NewsAdapter.NewsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int
    ) {
        val article = articleList[position]

        holder.binding.apply {
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvSource.text = article.source.toString()
            tvPublishedAt.text = article.publishedAt
            Glide.with(holder.itemView.context)
                .load(article.urlToImage)
                .into(ivArticleImage)
        }


    }

    override fun getItemCount(): Int {
        return articleList.size
    }
}