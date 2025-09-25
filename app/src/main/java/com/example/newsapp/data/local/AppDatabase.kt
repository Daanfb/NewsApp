package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.local.dao.RecentSearchDao
import com.example.newsapp.data.local.dao.RemoteKeyDao
import com.example.newsapp.data.local.dao.BookmarkedArticleDao
import com.example.newsapp.data.local.entity.ArticleCategoryCrossRef
import com.example.newsapp.data.local.entity.ArticleEntity
import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.RecentSearchEntity
import com.example.newsapp.data.local.entity.RemoteKeyEntity
import com.example.newsapp.data.local.entity.BookmarkedArticleEntity

@Database(
    entities = [ArticleEntity::class, RecentSearchEntity::class, BookmarkedArticleEntity::class, CategoryEntity::class, ArticleCategoryCrossRef::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    abstract fun getRecentSearchDao(): RecentSearchDao

    abstract fun getBookmarkedArticleDao(): BookmarkedArticleDao

    abstract fun getRemoteKeyDao(): RemoteKeyDao
}