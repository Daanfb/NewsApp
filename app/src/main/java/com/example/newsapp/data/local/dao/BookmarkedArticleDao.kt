package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.BookmarkedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: BookmarkedArticleEntity)

    @Query("DELETE FROM saved_articles WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT * FROM saved_articles ORDER BY bookmarkedAt DESC")
    fun observeAllBookmarkedArticlesFlow(): Flow<List<BookmarkedArticleEntity>>

    @Query("SELECT COUNT(*) FROM saved_articles WHERE url = :url")
    fun isBookmarkedFlow(url: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM saved_articles WHERE url = :url")
    suspend fun isBookmarked(url: String): Int
}