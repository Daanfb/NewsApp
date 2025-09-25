package com.example.newsapp.ui.screens.articledetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.BookmarkedArticleRepository
import com.example.newsapp.ui.core.navigation.ArticleDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class ArticleDetailsUiState(
    val isBookmarked: Boolean = false,
    val article: Article? = null,
)

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val savedArticlesRepository: BookmarkedArticleRepository
) : ViewModel() {

    private val articleEncode = savedStateHandle.toRoute<ArticleDetails>().articleEncode
    private val article = Json.decodeFromString<Article>(articleEncode)

    private val _uiState = MutableStateFlow(ArticleDetailsUiState(article = article))
    val uiState = _uiState.asStateFlow()

    init {
        observeIsArticleBookmarked()
    }

    private fun observeIsArticleBookmarked() {
        viewModelScope.launch {
            savedArticlesRepository.isArticleBookmarkedFlow(article.url).collect { isBookmarked ->
                _uiState.update { it.copy(isBookmarked = isBookmarked) }
            }
        }
    }

    fun onToggleBookmark() {
        viewModelScope.launch {
            savedArticlesRepository.toggleBookmarkedArticle(article)
        }
    }
}