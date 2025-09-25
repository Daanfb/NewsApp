package com.example.newsapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.local.dao.ArticleDao
import com.example.newsapp.data.remote.Constants
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.paging.GenericPagingSource
import com.example.newsapp.data.paging.NewsRemoteMediator
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsService: NewsApiService,
    private val db: AppDatabase,
    private val articleDao: ArticleDao,
) : NewsRepository {

    override suspend fun observeSearchNewsPager(
        query: String,
    ): Flow<PagingData<Article>> {

        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = Constants.PAGE_SIZE
            ),
            pagingSourceFactory = {
                GenericPagingSource { pageNumber ->
                    val response = newsService.searchNews(query = query.trim(), pageNumber = pageNumber)
                    if (response.isSuccessful) {
                        val body = response.body()
                        val networkArticles = body?.articles ?: emptyList()
                        networkArticles.map { it.toDomain() }
                    } else {
                        throw HttpException(response)
                    }
                }
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun observeTopHeadlinesPager(category: String): Flow<PagingData<Article>> {

        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = NewsRemoteMediator(
                category = category,
                newsService = newsService,
                db = db,
            ),
            pagingSourceFactory = {
                articleDao.getPagingSourceArticlesByCategory(category)
            }
        ).flow.map { pagingData ->
            pagingData.map { articleEntity ->
                articleEntity.toDomain()
            }
        }
    }
}