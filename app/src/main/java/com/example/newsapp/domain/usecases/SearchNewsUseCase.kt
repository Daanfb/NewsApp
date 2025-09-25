package com.example.newsapp.domain.usecases

import androidx.paging.PagingData
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val recentSearchRepository: RecentSearchRepository
) {

    /**
     * Save the search query to recent searches if it's not empty,
     * then fetch and return the search results for the given query and page.
     *
     * @param query The search query string.
     *
     * @return A list of articles matching the search query.
     */
    suspend operator fun invoke(query: String): Flow<PagingData<Article>> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            recentSearchRepository.insert(trimmedQuery)
        }

        return newsRepository.observeSearchNewsPager(trimmedQuery)
    }
}