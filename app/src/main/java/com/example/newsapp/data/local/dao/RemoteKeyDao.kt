package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKeyEntity: RemoteKeyEntity)

    @Query("SELECT * FROM remote_keys WHERE category = :category")
    suspend fun getRemoteKeyByCategory(category: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}