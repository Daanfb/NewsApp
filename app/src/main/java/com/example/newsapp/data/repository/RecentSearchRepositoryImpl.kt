package com.example.newsapp.data.repository

import com.example.newsapp.data.local.dao.RecentSearchDao
import com.example.newsapp.data.local.entity.RecentSearchEntity
import com.example.newsapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchDao: RecentSearchDao
) : RecentSearchRepository {

    companion object {
        private const val MAX_RECENT_SEARCHES = 10
    }

    override suspend fun insert(query: String) {
        val trimmedQuery = query.trim()

        return withContext(Dispatchers.IO) {
            val recentSearchEntity =
                RecentSearchEntity(query = trimmedQuery, timestamp = System.currentTimeMillis())
            recentSearchDao.insertRecentSearch(recentSearchEntity)

            val currentCount = recentSearchDao.count()
            if (currentCount > MAX_RECENT_SEARCHES) {
                val toDelete = currentCount - MAX_RECENT_SEARCHES
                recentSearchDao.deleteOldest(toDelete)
            }
        }
    }

    override fun observeRecentSearchesFlow(): Flow<List<String>> {
        return recentSearchDao.observeAllRecentSearchesFlow()
            .map { entities -> entities.map { it.query } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun delete(query: String) {
        return withContext(Dispatchers.IO) {
            recentSearchDao.delete(query)
        }
    }

    override suspend fun clearAll() {
        return withContext(Dispatchers.IO) {
            recentSearchDao.clearAll()
        }
    }
}