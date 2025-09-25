package com.example.newsapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.newsapp.data.local.entity.ArticleCategoryCrossRef
import com.example.newsapp.data.local.entity.ArticleEntity
import com.example.newsapp.data.local.entity.CategoryEntity

data class ArticleWithCategories(
    @Embedded val article: ArticleEntity,
    @Relation(
        parentColumn = "url",   // Primary key in ArticleEntity
        entityColumn = "name",  // Primary key in CategoryEntity
        associateBy = Junction(ArticleCategoryCrossRef::class)
    )
    val categories: List<CategoryEntity>
)
