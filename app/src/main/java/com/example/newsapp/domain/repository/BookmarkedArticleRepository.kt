package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarkedArticleRepository {

    /**
     * Observes the flow of bookmarked articles.
     *
     * @return A Flow emitting a list of bookmarked Articles.
     */
    fun observeBookmarkedArticlesFlow(): Flow<List<Article>>

    /**
     * Checks if a specific article is bookmarked.
     *
     * @param articleUrl The URL of the article to check.
     * @return A Flow emitting true if the article is bookmarked, false otherwise.
     */
    fun isArticleBookmarkedFlow(articleUrl: String): Flow<Boolean>

    /**
     * Toggles the bookmarked status of an article.
     * If the article is already bookmarked, it will be removed from bookmarks.
     * If it is not bookmarked, it will be added to bookmarks.
     *
     * @param article The Article to toggle bookmark status for.
     */
    suspend fun toggleBookmarkedArticle(article: Article)

    /**
     * Deletes a bookmarked article by its URL.
     *
     * @param articleUrl The URL of the article to be deleted from bookmarks.
     */
    suspend fun deleteBookmarkedArticle(articleUrl: String)

    /**
     * Inserts an article into the bookmarked articles.
     *
     * @param article The Article to be bookmarked.
     */
    suspend fun insertBookmarkedArticle(article: Article)
}