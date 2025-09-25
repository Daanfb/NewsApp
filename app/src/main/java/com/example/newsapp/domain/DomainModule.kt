package com.example.newsapp.domain

import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.data.repository.RecentSearchRepositoryImpl
import com.example.newsapp.data.repository.BookmarkedArticleRepositoryImpl
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.RecentSearchRepository
import com.example.newsapp.domain.repository.BookmarkedArticleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule{

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindRecentSearchRepository(
        recentSearchRepositoryImpl: RecentSearchRepositoryImpl
    ): RecentSearchRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkedArticleRepository(
        savedArticleRepositoryImpl: BookmarkedArticleRepositoryImpl
    ) : BookmarkedArticleRepository
}