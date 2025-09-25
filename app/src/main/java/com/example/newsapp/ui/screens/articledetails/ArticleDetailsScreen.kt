package com.example.newsapp.ui.screens.articledetails

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.core.components.AuthorAndDate
import com.example.newsapp.R
import com.example.newsapp.ui.core.components.formatRelativeTime

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleDetailsScreen(
    viewModel: ArticleDetailsViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scaleBookmarkIcon = remember { Animatable(1f) }

    LaunchedEffect(uiState.isBookmarked) {
        if (uiState.isBookmarked) {
            scaleBookmarkIcon.animateTo(1.3f, animationSpec = tween(150))
            scaleBookmarkIcon.animateTo(1f, animationSpec = tween(150))
        } else {
            scaleBookmarkIcon.animateTo(1f, animationSpec = tween(150))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        uiState.article?.let {
            ArticleDetailsContent(
                article = it,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }

        // Back and bookmark buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.38f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.article != null) {
                IconButton(
                    onClick = {
                        viewModel.onToggleBookmark()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.38f),
                        contentColor = Color.White
                    )
                ) {

                    val icon =
                        painterResource(if (uiState.isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark)


                    val contentDescription =
                        stringResource(if (uiState.isBookmarked) R.string.content_description_article_bookmarked else R.string.content_description_article_not_bookmarked)


                    Icon(
                        painter = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.scale(scaleBookmarkIcon.value)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ArticleDetailsContent(
    article: Article,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {


    with(sharedTransitionScope) {
        Box(modifier = Modifier.fillMaxWidth()) {

            LazyColumn {
                item {
                    AsyncImage(
                        model = article.urlToImage,
                        contentDescription = null,
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "image-${article.url}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .fillMaxWidth()
                    )
                }

                item("title") {
                    Text(
                        text = article.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                item("author_date") {
                    AuthorAndDate(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        author = article.author,
                        date = formatRelativeTime(article.publishedAt)
                    )
                }

                item("content") {
                    Text(
                        text = article.content ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
