package com.example.newsapp.ui.core.mappers

import androidx.annotation.StringRes
import com.example.newsapp.domain.model.NewsCategory
import com.example.newsapp.R

@StringRes
fun NewsCategory.toStringResId(): Int {
    return when (this) {
        NewsCategory.GENERAL -> R.string.category_general
        NewsCategory.BUSINESS -> R.string.category_business
        NewsCategory.ENTERTAINMENT -> R.string.category_entertainment
        NewsCategory.HEALTH -> R.string.category_health
        NewsCategory.SCIENCE -> R.string.category_science
        NewsCategory.SPORTS -> R.string.category_sports
        NewsCategory.TECHNOLOGY -> R.string.category_technology
    }
}