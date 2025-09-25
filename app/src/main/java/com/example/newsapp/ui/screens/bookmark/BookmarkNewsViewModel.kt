package com.example.newsapp.ui.screens.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.BookmarkedArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarkNewsUiState(
    val display: DisplayBookmarkNews = DisplayBookmarkNews.Loading
)

sealed interface DisplayBookmarkNews {
    object Loading : DisplayBookmarkNews
    data class Success(val articles: List<Article>) :
        DisplayBookmarkNews

    data class Error(val message: String) : DisplayBookmarkNews
    object Empty: DisplayBookmarkNews
}

@HiltViewModel
class BookmarkNewsViewModel @Inject constructor(private val savedArticleRepository: BookmarkedArticleRepository): ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkNewsUiState())
    val uiState = _uiState.asStateFlow()

    private var lastDeletedArticle: Article? = null

    init {
        observeBookmarkedArticles()
    }

    fun observeBookmarkedArticles(){
        _uiState.update {
            it.copy(display = DisplayBookmarkNews.Loading)
        }

        viewModelScope.launch {
            try {

                savedArticleRepository.observeBookmarkedArticlesFlow().collect { articles ->
                    if(articles.isNotEmpty()){
                        _uiState.update {
                            it.copy(display = DisplayBookmarkNews.Success(articles))
                        }
                    }else{
                        _uiState.update {
                            it.copy(display = DisplayBookmarkNews.Empty)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(display = DisplayBookmarkNews.Error(e.localizedMessage ?: "An unexpected error occurred"))
                }
            }
        }
    }

    fun deleteBookmarkedArticle(article: Article) {
        viewModelScope.launch {
            savedArticleRepository.deleteBookmarkedArticle(article.url)
            lastDeletedArticle = article
        }
    }

    fun undoDelete(){
        lastDeletedArticle?.let { article ->
            viewModelScope.launch {
                savedArticleRepository.insertBookmarkedArticle(article)
            }
            lastDeletedArticle = null
        }
    }
}