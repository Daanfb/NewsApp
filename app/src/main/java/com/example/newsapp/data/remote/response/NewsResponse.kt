package com.example.newsapp.data.remote.response

import com.example.newsapp.data.local.entity.ArticleEntity
import com.example.newsapp.domain.model.Article
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class NewsResponse(
    val articles: List<ArticleNetwork>,
    val status: String,
    val totalResults: Int
)

data class ArticleNetwork(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceNetwork?,
    val title: String?,
    val url: String,
    val urlToImage: String?
){
    @OptIn(ExperimentalTime::class)
    fun toEntity(): ArticleEntity{

        // Date format from API is ISO 8601, converting to epoch millis
        // Example: 2023-10-05T14:48:00Z
        val publishedAt = publishedAt?.let {
            try {
                Instant.parse(it).toEpochMilliseconds()
            } catch (e: Exception) {
                null
            }
        }

        return ArticleEntity(
            url = url,
            author = author,
            content = content,
            description = description,
            publishedAt = publishedAt,
            sourceName = source?.name,
            title = title,
            urlToImage = urlToImage
        )
    }

    fun toDomain(): Article = this.toEntity().toDomain()
}

data class SourceNetwork(
    val id: String?,
    val name: String?
)
