package com.example.newsapp.ui.screens.searchnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.RecentSearchRepository
import com.example.newsapp.domain.usecases.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchNewsUiState(
    val query: String = "",
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
)

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase,
    private val newsRepository: NewsRepository,
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchNewsUiState())
    val uiState = _uiState.asStateFlow()

    private val _articles = MutableStateFlow(PagingData.empty<Article>())
    val articles = _articles.asStateFlow()


    init {
        getRecentSearches()
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun searchNews() {
        viewModelScope.launch {
            val query = _uiState.value.query

            _uiState.update { it.copy(isSearching = true) }

            searchNewsUseCase(query = query)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _articles.update { pagingData }
                }
        }
    }

    fun resetSearch() {
        _uiState.update {
            it.copy(
                query = "",
                isSearching = false,
            )
        }
        _articles.update { PagingData.empty() }
    }

    fun getRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.observeRecentSearchesFlow().collect { searches ->
                _uiState.update { it.copy(recentSearches = searches) }
            }
        }
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.delete(query)

        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearAll()
        }
    }
}