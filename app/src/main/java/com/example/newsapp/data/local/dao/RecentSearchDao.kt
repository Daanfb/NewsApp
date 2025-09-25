package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(query: RecentSearchEntity)

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC")
    fun observeAllRecentSearchesFlow(): Flow<List<RecentSearchEntity>>

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun delete(query: String)

    @Query("SELECT COUNT(*) FROM recent_searches")
    suspend fun count(): Int

    @Query("""
        DELETE FROM recent_searches 
        WHERE query IN (
            SELECT query FROM recent_searches ORDER BY timestamp ASC LIMIT :toDelete
        )
    """)
    suspend fun deleteOldest(toDelete: Int)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}