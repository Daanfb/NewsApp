package com.example.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.domain.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: Long?,
    val sourceName: String?,
    val title: String?,
    val urlToImage: String?
){
    fun toDomain(): Article{
        return Article(
            url = url,
            author = author,
            content = content,
            description = description,
            publishedAt = publishedAt,
            sourceName = sourceName,
            title = title,
            urlToImage = urlToImage
        )
    }
}
