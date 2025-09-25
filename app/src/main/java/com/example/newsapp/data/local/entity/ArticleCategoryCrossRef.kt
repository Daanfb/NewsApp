package com.example.newsapp.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "article_category_cross_ref",
    primaryKeys = ["articleUrl", "categoryName"]
)
data class ArticleCategoryCrossRef(
    val articleUrl: String,
    val categoryName: String
)
