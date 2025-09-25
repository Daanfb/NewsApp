package com.example.newsapp.ui.core.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleItem(
    article: Article, sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope, onArticleClick: () -> Unit = {}
) {

    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onArticleClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(article.urlToImage)
                    .crossfade(true).build(),
                contentDescription = null,
                error = painterResource(R.drawable.ic_no_image),
                fallback = painterResource(R.drawable.ic_no_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.3f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .sharedElement(
                        sharedContentState = sharedTransitionScope.rememberSharedContentState(key = "image-${article.url}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = article.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                AuthorAndDate(
                    modifier = Modifier.fillMaxWidth(),
                    author = article.author,
                    date = formatRelativeTime(article.publishedAt)
                )
            }
        }
    }
}

@Composable
fun AuthorAndDate(modifier: Modifier = Modifier, author: String?, date: String?) {

    val text = when {
        author != null && date != null -> "$author · $date"
        author != null -> author
        date != null -> date
        else -> ""
    }

    Row(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ShimmerArticleList(itemCount: Int = 8) {
    Column {
        repeat(itemCount) {
            ShimmerArticleItem()
        }
    }
}

@Composable
private fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

@Composable
private fun ShimmerArticleItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .weight(0.3f)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(shimmerBrush())
        )

        // Text placeholders
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush())
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Un poco más corto
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush())
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush())
            )
        }
    }
}

@Composable
fun formatRelativeTime(
    millis: Long?
): String?{
    if (millis == null) return null

    val difMillis = System.currentTimeMillis() - millis

    val seconds = difMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> stringResource(R.string.now)
        minutes < 60 -> stringResource(R.string.minutes_ago, minutes)
        hours < 24 -> stringResource(R.string.hours_ago, hours)
        days < 7 -> stringResource(R.string.days_ago, days)
        else -> {
            val instant = Instant.ofEpochMilli(millis)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())
            return formatter.format(instant)
        }
    }
}