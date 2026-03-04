package com.example.alldocumentsreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_documents")
data class RecentDocumentEntity(
    @PrimaryKey val uri: String,
    val displayName: String,
    val type: String,
    val size: Long,
    val lastOpenedAt: Long,
)

@Entity(tableName = "favorite_documents")
data class FavoriteDocumentEntity(
    @PrimaryKey val uri: String,
    val displayName: String,
    val type: String,
    val size: Long,
    val favoritedAt: Long,
)
