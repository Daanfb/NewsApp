package com.example.newsapp.ui.screens.bookmark

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.core.components.ArticleItem
import com.example.newsapp.ui.core.components.ErrorMessageTemplates
import com.example.newsapp.ui.core.components.MessageTemplate
import com.example.newsapp.ui.core.components.ShimmerArticleList
import com.example.newsapp.ui.core.components.fadingEdge
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookmarkNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarkNewsViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navToArticleDetails: (Article) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var snackbackJob by remember { mutableStateOf<Job?>(null) }

    val articleUnbookmarkedString = stringResource(R.string.article_unbookmarked)
    val undoString = stringResource(R.string.undo)

    Scaffold(modifier = modifier, snackbarHost = {
        SnackbarHost(snackbarHostState)
    }) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {

            Text(
                text = stringResource(R.string.bookmarked_news),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            when (val display = uiState.display) {
                DisplayBookmarkNews.Empty -> EmptyBookmarkNews()
                is DisplayBookmarkNews.Error -> ErrorMessageTemplates.UnexpectedError()
                DisplayBookmarkNews.Loading -> ShimmerArticleList()
                is DisplayBookmarkNews.Success -> ContentBookmarkNews(
                    articles = display.articles,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    navToArticleDetails = navToArticleDetails,
                    onUnbookmark = { article ->
                        viewModel.deleteBookmarkedArticle(article)

                        // Cancel any existing snackbar job to avoid multiple snackbars
                        snackbackJob?.cancel()

                        snackbackJob = coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = articleUnbookmarkedString,
                                withDismissAction = true,
                                actionLabel = undoString,
                                duration = SnackbarDuration.Short
                            )

                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    viewModel.undoDelete()
                                }

                                SnackbarResult.Dismissed -> {
                                    snackbackJob?.cancel()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun EmptyBookmarkNews() {
    MessageTemplate(
        icon = painterResource(R.drawable.ic_not_found),
        title = stringResource(R.string.no_bookmarks),
        message = stringResource(R.string.no_bookmarks_message)
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ContentBookmarkNews(
    articles: List<Article>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navToArticleDetails: (Article) -> Unit,
    onUnbookmark: (Article) -> Unit = {}
) {

    val topFade = Brush.verticalGradient(
        0f to Color.Transparent,
        0.05f to Color.Red,
        1f to Color.Red,
    )

    LazyColumn(
        modifier = Modifier.animateContentSize().fadingEdge(topFade),
        contentPadding = PaddingValues(top = 8.dp)
    ) {

        items(articles, key = { it.url }) { article ->
            ArticleItemWithSwipeToDelete(
                article = article,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onArticleClick = { navToArticleDetails(article) },
                onUnbookmark = { onUnbookmark(article) })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ArticleItemWithSwipeToDelete(
    article: Article,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onArticleClick: () -> Unit,
    onUnbookmark: () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) onUnbookmark()
            true
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxSize(),
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart ->
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.content_description_unbookmark_article),
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp)
                    )

                else -> {}
            }
        }
    ) {
        ArticleItem(
            article = article,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onArticleClick = onArticleClick
        )
    }
}

