package com.example.newsapp.ui.core.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class MainDestinations{

    @Serializable
    data object TopHeadlinesNews: MainDestinations()

    @Serializable
    data object SearchNews: MainDestinations()

    @Serializable
    data object BookmarksNews: MainDestinations()
}

/**
 * The recommended way to pass objects between destinations is to send only the unique identifier and then get the full object from the repository.
 * However, for simplicity, the whole object will be sent as a JSON string
 */
@Serializable
data class ArticleDetails(val articleEncode: String)