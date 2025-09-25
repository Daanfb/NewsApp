package com.example.newsapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.local.entity.ArticleEntity
import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.RemoteKeyEntity
import com.example.newsapp.data.remote.NewsApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val category: String,
    private val newsService: NewsApiService,
    private val db: AppDatabase,
) : RemoteMediator<Int, ArticleEntity>() {

    val articleDao = db.getArticleDao()
    val remoteKeyDao = db.getRemoteKeyDao()

    /**
     * If you want to enable cache timeout, uncomment the below code.
     */
//    override suspend fun initialize(): InitializeAction {
//        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
//
//        val lastUpdated = remoteKeyDao.getRemoteKeyByCategory(category)?.lastUpdated ?: 0L
//
//        return if (System.currentTimeMillis() - lastUpdated >= cacheTimeout) {
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        } else {
//            InitializeAction.SKIP_INITIAL_REFRESH
//        }
//    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {

        return try {

            // 1. Calculate the page number to load
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getRemoteKeyByCategory(category)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // 2. Make the network request
            val response = newsService.getTopHeadlines(
                category = category,
                pageNumber = loadKey,
                pageSize = state.config.pageSize
            )

            if (response.isSuccessful) {
                val body = response.body()
                val articles = body?.articles ?: emptyList()
                val endOfPaginationReached = articles.isEmpty()

                // 3. Save the data to the database
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        articleDao.clearAllArticlesByCategory(category)
                        remoteKeyDao.deleteByCategory(category)
                    }

                    // Save the news remote keys and the articles
                    val nextKey = if (endOfPaginationReached) null else loadKey + 1
                    remoteKeyDao.insertOrReplace(
                        RemoteKeyEntity(
                            category = category,
                            nextKey = nextKey,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )

                    val articleEntities = articles.map { it.toEntity() }
                    val categoryEntity = CategoryEntity(name = category)
                    articleDao.insertAllArticlesForCategory(
                        articles = articleEntities,
                        category = categoryEntity
                    )
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(HttpException(response))
            }

        } catch (e: IOException) {

            // Check if we have any cached data
            val hasCachedData = articleDao.countArticlesByCategory(category) > 0

            return if(hasCachedData){
                MediatorResult.Success(endOfPaginationReached = true)
            }else{
                MediatorResult.Error(e)
            }

        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}