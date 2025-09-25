package com.example.newsapp.data.repository

import androidx.room.withTransaction
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.local.dao.BookmarkedArticleDao
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.BookmarkedArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkedArticleRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val bookmarkedArticleDao: BookmarkedArticleDao
) : BookmarkedArticleRepository {

    override fun observeBookmarkedArticlesFlow(): Flow<List<Article>> {
        return bookmarkedArticleDao.observeAllBookmarkedArticlesFlow()
            .map { savedArticleEntities -> savedArticleEntities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun isArticleBookmarkedFlow(articleUrl: String): Flow<Boolean> {
        return bookmarkedArticleDao.isBookmarkedFlow(articleUrl)
            .map { count -> count > 0 }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun toggleBookmarkedArticle(article: Article) {
        db.withTransaction {
            val exists = bookmarkedArticleDao.isBookmarked(article.url)
            if (exists > 0) {
                bookmarkedArticleDao.deleteByUrl(article.url)
            } else {
                bookmarkedArticleDao.insert(article.toBookmarkedEntity(bookmarkedAt = System.currentTimeMillis()))
            }
        }
    }

    override suspend fun deleteBookmarkedArticle(articleUrl: String) {
        bookmarkedArticleDao.deleteByUrl(articleUrl)
    }

    override suspend fun insertBookmarkedArticle(article: Article) {
        bookmarkedArticleDao.insert(article.toBookmarkedEntity(bookmarkedAt = article.bookmarkedAt ?: System.currentTimeMillis()))
    }
}