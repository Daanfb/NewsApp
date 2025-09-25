package com.example.newsapp.ui.screens.topheadlines

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import com.example.newsapp.R
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsCategory
import com.example.newsapp.ui.core.components.ArticleItem
import com.example.newsapp.ui.core.components.ErrorMessageTemplates
import com.example.newsapp.ui.core.components.ShimmerArticleList
import com.example.newsapp.ui.core.components.fadingEdge
import com.example.newsapp.ui.core.mappers.toStringResId
import com.example.newsapp.ui.screens.searchnews.LoadingIndicator
import kotlinx.coroutines.launch
import okio.IOException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopHeadlinesNewsScreen(
    modifier: Modifier = Modifier,
    viewModel: TopHeadlinesNewsViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navToArticleDetails: (Article) -> Unit
) {

    val category by viewModel.category.collectAsStateWithLifecycle()
    val articles = viewModel.articles.collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    Scaffold(modifier = modifier, floatingActionButton = {
        AnimatedVisibility(
            visible = !isAtTop,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }) {
            SmallFloatingActionButton(onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
            }
        }
    }) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.top_headlines),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            RowCategories(
                currentCategory = category,
                listState = listState,
                onCategoryClicked = viewModel::onCategorySelected
            )

            when (val refreshState = articles.loadState.refresh) {
                LoadState.Loading -> ShimmerArticleList()

                is LoadState.NotLoading -> {
                    ContentTopHeadlines(
                        articles = articles,
                        listState = listState,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        navToArticleDetails = navToArticleDetails
                    )
                }

                is LoadState.Error -> {
                    val error = refreshState.error

                    when (error) {
                        is IOException -> ErrorMessageTemplates.InternetConnectionError()
                        else -> ErrorMessageTemplates.UnexpectedError()
                    }
                }
            }
        }
    }
}

@Composable
private fun RowCategories(
    currentCategory: NewsCategory,
    listState: LazyListState,
    onCategoryClicked: (NewsCategory) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val horizontalFade = Brush.horizontalGradient(
        0f to Color.Transparent,
        0.05f to Color.Red,
        0.95f to Color.Red,
        1f to Color.Transparent
    )

    LazyRow(
        modifier = Modifier.fadingEdge(horizontalFade),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(NewsCategory.entries) { category ->
            InputChip(
                selected = category == currentCategory,
                onClick = {
                    if (category != currentCategory) {
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    }
                    onCategoryClicked(category)
                },

                label = { Text(text = stringResource(category.toStringResId())) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ContentTopHeadlines(
    articles: LazyPagingItems<Article>,
    listState: LazyListState,
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
        state = listState,
        contentPadding = PaddingValues(top = 8.dp),
        modifier = Modifier.fadingEdge(topFade)
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
