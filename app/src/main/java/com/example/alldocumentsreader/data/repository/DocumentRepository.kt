package com.example.alldocumentsreader.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.alldocumentsreader.data.local.FavoriteDocumentDao
import com.example.alldocumentsreader.data.local.FavoriteDocumentEntity
import com.example.alldocumentsreader.data.local.RecentDocumentDao
import com.example.alldocumentsreader.data.local.RecentDocumentEntity
import com.example.alldocumentsreader.data.preferences.PreferenceManager
import com.example.alldocumentsreader.data.saf.SafScanner
import com.example.alldocumentsreader.domain.model.DocCategory
import com.example.alldocumentsreader.domain.model.DocumentItem
import com.example.alldocumentsreader.domain.model.SortField
import com.example.alldocumentsreader.domain.model.SortOption
import com.example.alldocumentsreader.domain.model.SortOrder
import com.example.alldocumentsreader.domain.model.matches
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DocumentRepository(
    private val context: Context,
    private val recentDao: RecentDocumentDao,
    private val favoriteDao: FavoriteDocumentDao,
    private val prefs: PreferenceManager,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scanner = SafScanner(context)
    private val scanCache = MutableStateFlow<List<DocumentItem>>(emptyList())

    val onboardingDone = prefs.onboardingDone
    val language = prefs.language
    val selectedFolderUri = prefs.selectedFolderUri

    fun scanDocuments(folderUri: Uri): Flow<List<DocumentItem>> = flow {
        val favorites = favoriteDao.getAllUris().toSet()
        val scanned = scanner.scan(folderUri).map { it.copy(isFavorite = favorites.contains(it.uri.toString())) }
        scanCache.value = scanned
        emit(scanned)
    }.flowOn(io)

    fun observeRecent() = recentDao.observeAll()
    fun observeFavorites() = favoriteDao.observeAll()

    suspend fun setLanguage(language: String) = prefs.setLanguage(language)
    suspend fun setOnboardingDone(done: Boolean) = prefs.setOnboardingDone(done)
    suspend fun setFolderUri(uri: String) = prefs.setFolderUri(uri)

    fun sortOption(category: DocCategory) = prefs.sortOption(category)
    suspend fun saveSort(category: DocCategory, option: SortOption) = prefs.saveSort(category, option)

    fun filteredDocuments(category: DocCategory, query: String, sort: SortOption): Flow<List<DocumentItem>> =
        scanCache.map { docs ->
            docs.asSequence()
                .filter { category.matches(it.extension) }
                .filter { query.isBlank() || it.displayName.contains(query, ignoreCase = true) }
                .sortedWith(sortComparator(sort))
                .toList()
        }

    suspend fun openDocument(item: DocumentItem) {
        withContext(io) {
            recentDao.upsert(
                RecentDocumentEntity(
                    uri = item.uri.toString(),
                    displayName = item.displayName,
                    type = item.extension,
                    size = item.size,
                    lastOpenedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun toggleFavorite(item: DocumentItem) {
        withContext(io) {
            val uri = item.uri.toString()
            if (item.isFavorite) favoriteDao.delete(uri)
            else favoriteDao.upsert(
                FavoriteDocumentEntity(
                    uri = uri,
                    displayName = item.displayName,
                    type = item.extension,
                    size = item.size,
                    favoritedAt = System.currentTimeMillis(),
                )
            )
            scanCache.value = scanCache.value.map {
                if (it.uri == item.uri) it.copy(isFavorite = !item.isFavorite) else it
            }
        }
    }

    fun takePersistablePermission(folderUri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            folderUri,
            IntentFlags.readFlags,
        )
    }

    object IntentFlags {
        val readFlags: Int = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    private fun sortComparator(sort: SortOption): Comparator<DocumentItem> {
        val base = when (sort.field) {
            SortField.TITLE -> compareBy<DocumentItem> { it.displayName.lowercase() }
            SortField.TYPE -> compareBy { it.extension }
            SortField.DATE -> compareBy { it.lastModified }
            SortField.SIZE -> compareBy { it.size }
        }
        return if (sort.order == SortOrder.ASC) base else base.reversed()
    }
}
