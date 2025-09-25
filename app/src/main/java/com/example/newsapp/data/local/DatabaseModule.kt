package com.example.newsapp.data.local

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.local.dao.RecentSearchDao
import com.example.newsapp.data.local.dao.RemoteKeyDao
import com.example.newsapp.data.local.dao.BookmarkedArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "news_app_db.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase): ArticleDao {
        return database.getArticleDao()
    }

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: AppDatabase): RecentSearchDao {
        return database.getRecentSearchDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkedArticleDao(database: AppDatabase): BookmarkedArticleDao {
        return database.getBookmarkedArticleDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: AppDatabase): RemoteKeyDao {
        return database.getRemoteKeyDao()
    }
}