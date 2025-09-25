package com.example.newsapp.ui.screens.searchnews

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.example.newsapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.core.components.ArticleItem
import com.example.newsapp.ui.core.components.ComposableAnimationLayout
import com.example.newsapp.ui.core.components.ErrorMessageTemplates
import com.example.newsapp.ui.core.components.MessageTemplate
import com.example.newsapp.ui.core.components.ShimmerArticleList
import com.example.newsapp.ui.core.components.fadingEdge
import okio.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchNewsViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navToArticleDetails: (Article) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.articles.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTextField(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                showBackButton = uiState.isSearching,
                onBackClick = viewModel::resetSearch,
                onImeActionClick = {
                    if (uiState.query.isNotBlank()) {
                        viewModel.searchNews()
                    }
                }
            )
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = paddingValues.calculateTopPadding())
                .padding(top = 8.dp)
        ) {

            if (uiState.isSearching) {

                when (val refreshState = articles.loadState.refresh) {
                    is LoadState.Loading -> ShimmerArticleList()

                    is LoadState.Error -> {
                        val error = refreshState.error

                        when (error) {
                            is IOException -> ErrorMessageTemplates.InternetConnectionError()
                            else -> ErrorMessageTemplates.UnexpectedError()
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (articles.itemCount == 0) {
                            EmptySearchNews()
                        } else {
                            ContentSearchNews(
                                articles = articles,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                navToArticleDetails = { article ->
                                    navToArticleDetails(article)
                                })
                        }
                    }
                }
            } else {
                RecentQueries(
                    recentQueries = uiState.recentSearches,
                    onQueryClick = { query ->
                        viewModel.onQueryChange(query)
                        viewModel.searchNews()
                    },
                    onDeleteQueryClick = { query ->
                        viewModel.deleteRecentSearch(query)
                    },
                    onClearAllClick = {
                        viewModel.clearAllRecentSearches()
                    }
                )
            }
        }
    }
}

@Composable
private fun RecentQueries(
    recentQueries: List<String>,
    onQueryClick: (String) -> Unit,
    onDeleteQueryClick: (String) -> Unit,
    onClearAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recent_searches),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onClearAllClick
            ) {
                Text(text = stringResource(R.string.clear_all))
            }
        }

        // List of recent queries
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(recentQueries) { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onQueryClick(query) })
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = query,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    IconButton(onClick = { onDeleteQueryClick(query) }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.content_description_delete_recent_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ContentSearchNews(
    articles: LazyPagingItems<Article>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navToArticleDetails: (Article) -> Unit
) {

    val topFade = Brush.verticalGradient(
        0f to Color.Transparent,
        0.05f to Color.Red,
        1f to Color.Red,
    )

    LazyColumn(
        modifier = Modifier.fadingEdge(topFade),
        contentPadding = PaddingValues(top = 8.dp),
    ) {
        items(articles.itemCount, key = articles.itemKey { it.url }) { index ->
            articles[index]?.let { article ->
                ArticleItem(
                    article = article,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onArticleClick = {
                        navToArticleDetails(article)
                    })
            }
        }

        when (articles.loadState.append) {
            is LoadState.Loading -> item {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    loadingIconSize = 24.dp
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun EmptySearchNews() {
    MessageTemplate(
        icon = painterResource(R.drawable.ic_not_found),
        title = stringResource(R.string.no_news_found),
        message = stringResource(R.string.try_search_different_keyword)
    )
}

@Composable
fun LoadingIndicator(modifier: Modifier, loadingIconSize: Dp = 56.dp) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(loadingIconSize))
    }
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onImeActionClick: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val progress by animateFloatAsState(
        if (showBackButton) 1f else 0f,
        animationSpec = tween(300)
    )

    ComposableAnimationLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        progress = progress
    ) {

        IconButton(onClick = {
            focusManager.clearFocus()
            onBackClick()
        }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_description_see_recent_searches),
                modifier = Modifier.size(28.dp)
            )
        }

        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text(text = stringResource(R.string.search_news))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text).copy(
                imeAction = ImeAction.Search
            ),
            shape = CircleShape,
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    onImeActionClick()
                }
            ),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            )
        )
    }
}