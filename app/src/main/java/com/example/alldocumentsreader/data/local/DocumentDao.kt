package com.example.alldocumentsreader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: RecentDocumentEntity)

    @Query("SELECT * FROM recent_documents ORDER BY lastOpenedAt DESC")
    fun observeAll(): Flow<List<RecentDocumentEntity>>
}

@Dao
interface FavoriteDocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: FavoriteDocumentEntity)

    @Query("DELETE FROM favorite_documents WHERE uri = :uri")
    suspend fun delete(uri: String)

    @Query("SELECT * FROM favorite_documents ORDER BY favoritedAt DESC")
    fun observeAll(): Flow<List<FavoriteDocumentEntity>>

    @Query("SELECT uri FROM favorite_documents")
    suspend fun getAllUris(): List<String>
}
