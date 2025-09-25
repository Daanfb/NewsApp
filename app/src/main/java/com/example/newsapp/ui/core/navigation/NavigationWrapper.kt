package com.example.newsapp.ui.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.core.navigation.bottomNavigationBar.BottomNavigationBar
import com.example.newsapp.ui.screens.bookmark.BookmarkNewsScreen
import com.example.newsapp.ui.screens.topheadlines.TopHeadlinesNewsScreen
import com.example.newsapp.ui.screens.articledetails.ArticleDetailsScreen
import com.example.newsapp.ui.screens.searchnews.SearchNewsScreen
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationWrapper() {

    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = MainDestinations.TopHeadlinesNews
        ) {
            composable<MainDestinations.TopHeadlinesNews> {
                MainContainer(
                    primaryNavController = navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }

            composable<ArticleDetails> { backStackEntry ->
                ArticleDetailsScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContainer(
    primaryNavController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val nestedNavController = rememberNavController()

    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
        with(animatedVisibilityScope) {
            with(sharedTransitionScope) {

                BottomNavigationBar(
                    navController = nestedNavController,
                    currentDestination = currentDestination,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(
                            zIndexInOverlay = 1f,
                        )
                        .animateEnterExit(
                            enter = fadeIn(nonSpatialExpressiveSpring()) + slideInVertically(
                                spatialExpressiveSpring(),
                            ) {
                                it
                            },
                            exit = fadeOut(nonSpatialExpressiveSpring()) + slideOutVertically(
                                spatialExpressiveSpring(),
                            ) {
                                it
                            },
                        )
                )
            }
        }
    })
    { paddingValues ->

        val commonModifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues)

        NavHost(
            navController = nestedNavController,
            startDestination = MainDestinations.TopHeadlinesNews
        ) {
            composable<MainDestinations.TopHeadlinesNews> {

                TopHeadlinesNewsScreen(
                    modifier = commonModifier,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    navToArticleDetails = { article ->

                        val encodeArticle = Json.encodeToString(Article.serializer(), article)
                        primaryNavController.navigate(ArticleDetails(encodeArticle))
                    })
            }

            composable<MainDestinations.SearchNews> {
                SearchNewsScreen(
                    modifier = commonModifier,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    navToArticleDetails = { article ->

                        val encodeArticle = Json.encodeToString(Article.serializer(), article)
                        primaryNavController.navigate(ArticleDetails(encodeArticle))
                    })
            }

            composable<MainDestinations.BookmarksNews> {
                BookmarkNewsScreen(
                    modifier = commonModifier,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    navToArticleDetails = { article ->

                        val encodeArticle = Json.encodeToString(Article.serializer(), article)
                        primaryNavController.navigate(ArticleDetails(encodeArticle))
                    })
            }

        }
    }
}


fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f,
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f,
)

