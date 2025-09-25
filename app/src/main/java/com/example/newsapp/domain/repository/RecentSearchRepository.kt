package com.example.newsapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {

    /**
     * Inserts a recent search query into the local database.
     *
     * @param query The search query string to be inserted.
     */
    suspend fun insert(query: String)

    /**
     * Observes the flow of recent search queries.
     *
     * @return A Flow emitting a list of recent search query strings.
     */
    fun observeRecentSearchesFlow(): Flow<List<String>>

    /**
     * Deletes a specific search query from the local database.
     *
     * @param query The search query string to be deleted.
     */
    suspend fun delete(query: String)

    /**
     * Clears all recent search queries from the local database.
     */
    suspend fun clearAll()
}