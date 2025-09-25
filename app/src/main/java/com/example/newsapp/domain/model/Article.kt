package com.example.newsapp.domain.model

import com.example.newsapp.data.local.entity.BookmarkedArticleEntity
import kotlinx.serialization.Serializable

/**
 * Data class representing a news article.
 *
 * @property url The URL of the article.
 * @property author The author of the article (nullable).
 * @property content The content of the article (nullable).
 * @property title The title of the article (nullable).
 * @property description A brief description of the article (nullable).
 * @property urlToImage The URL to the image associated with the article (nullable).
 * @property publishedAt The publication timestamp of the article (nullable).
 * @property sourceName The name of the source of the article (nullable).
 * @property bookmarkedAt The timestamp when the article was bookmarked (nullable, default is null).
 */
@Serializable
data class Article(
    val url: String,
    val author: String?,
    val content: String?,
    val title: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: Long?,
    val sourceName: String?,
    val bookmarkedAt: Long? = null
){

    fun toBookmarkedEntity(bookmarkedAt: Long): BookmarkedArticleEntity {
        return BookmarkedArticleEntity(
            url = url,
            author = author,
            content = content,
            description = description,
            publishedAt = publishedAt,
            sourceName = sourceName,
            title = title,
            urlToImage = urlToImage,
            bookmarkedAt = bookmarkedAt
        )
    }
}

enum class NewsCategory{
    GENERAL,
    BUSINESS,
    ENTERTAINMENT,
    HEALTH,
    SCIENCE,
    SPORTS,
    TECHNOLOGY
}