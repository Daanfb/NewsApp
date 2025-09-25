package com.example.newsapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.newsapp.data.local.entity.ArticleCategoryCrossRef
import com.example.newsapp.data.local.entity.ArticleEntity
import com.example.newsapp.data.local.entity.CategoryEntity

@Dao
interface ArticleDao {

    // INSERTS METHODS

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllArticleCategoryCrossRefs(crossRef: List<ArticleCategoryCrossRef>)

    @Transaction
    suspend fun insertAllArticlesForCategory(articles: List<ArticleEntity>, category: CategoryEntity) {
        // 1. Insert the category
        insertCategory(category)

        // 2. Insert articles
        insertAllArticles(articles)

        // 3. Create and insert cross references
        val crossRefs = articles.map { article ->
            ArticleCategoryCrossRef(articleUrl = article.url, categoryName = category.name)
        }

        insertAllArticleCategoryCrossRefs(crossRefs)
    }

    // GET METHODS

    @Query("""
        SELECT * FROM articles
        INNER JOIN article_category_cross_ref
        ON articles.url = article_category_cross_ref.articleUrl
        WHERE article_category_cross_ref.categoryName = :category
        ORDER BY articles.publishedAt DESC
        """)
    fun getPagingSourceArticlesByCategory(category: String): PagingSource<Int, ArticleEntity>

    @Query("""
    SELECT COUNT(articles.url) FROM articles
    INNER JOIN article_category_cross_ref
    ON articles.url = article_category_cross_ref.articleUrl
    WHERE article_category_cross_ref.categoryName = :category
    """)
    suspend fun countArticlesByCategory(category: String): Int

    // DELETE METHODS

    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    @Query("""
        DELETE FROM articles WHERE url IN (
            SELECT url FROM article_category_cross_ref
            WHERE categoryName = :category
        )
    """)
    suspend fun clearAllArticlesByCategory(category: String)
}