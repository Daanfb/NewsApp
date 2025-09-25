package com.example.newsapp.ui.core.navigation.bottomNavigationBar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.newsapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.example.newsapp.ui.core.navigation.MainDestinations

data class BottomNavBarItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val route: MainDestinations
)

object BottomBarConstants {
    val BottomNavItems = listOf(
        BottomNavBarItem(
            label = R.string.top_headlines,
            icon = R.drawable.ic_breaking_news,
            route = MainDestinations.TopHeadlinesNews
        ),
        BottomNavBarItem(
            label = R.string.search,
            icon = R.drawable.ic_search,
            route = MainDestinations.SearchNews
        ),
        BottomNavBarItem(
            label = R.string.bookmarks,
            icon = R.drawable.ic_bookmarks,
            route = MainDestinations.BookmarksNews
        )
    )
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentDestination: NavDestination?,
) {
    NavigationBar(modifier = modifier) {

        BottomBarConstants.BottomNavItems.forEach { tab ->

            val isSelected =
                currentDestination?.hierarchy?.any { it.route == tab.route::class.qualifiedName } == true

            NavigationBarItem(
                selected = isSelected,
                icon = {
                    Icon(painter = painterResource(tab.icon), contentDescription = null)
                },
                label = {
                    Text(
                        text = stringResource(tab.label), style =
                            if (isSelected) {
                                MaterialTheme.typography.labelMedium
                            } else {
                                MaterialTheme.typography.bodySmall
                            }
                    )
                },
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                        restoreState = true

                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                    }
                })
        }
    }
}