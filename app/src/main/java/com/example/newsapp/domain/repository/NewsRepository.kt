package com.example.newsapp.domain.repository

import androidx.paging.PagingData
import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    /**
     * Observe a Flow of PagingData containing Articles based on the search query.
     *
     * @param query The search query string.
     * @return A Flow emitting PagingData of Articles.
     */
    suspend fun observeSearchNewsPager(query: String): Flow<PagingData<Article>>

    /**
     * Observe a Flow of PagingData containing top headline Articles for a specific category.
     *
     * @param category The news category (e.g., "business", "sports").
     * @return A Flow emitting PagingData of Articles.
     */
    suspend fun observeTopHeadlinesPager(category: String): Flow<PagingData<Article>>
}