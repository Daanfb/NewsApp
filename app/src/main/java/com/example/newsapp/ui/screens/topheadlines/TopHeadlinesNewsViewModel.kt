package com.example.newsapp.ui.screens.topheadlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsCategory
import com.example.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TopHeadlinesNewsViewModel @Inject constructor(private val newsRepository: NewsRepository) :
    ViewModel() {

    private val _category = MutableStateFlow(NewsCategory.GENERAL)
    val category = _category.asStateFlow()

    /**
     * A Flow of PagingData containing news articles for the selected category.
     * This Flow updates whenever the selected category changes.
     * The articles are cached in the ViewModel's scope to survive configuration changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val articles: Flow<PagingData<Article>> = _category
        .flatMapLatest { newsCategory ->
            newsRepository.observeTopHeadlinesPager(newsCategory.name.lowercase())
        }
        .cachedIn(viewModelScope)

    fun onCategorySelected(category: NewsCategory) {
        _category.update { category }
    }
}