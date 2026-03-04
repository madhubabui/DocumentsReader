package com.example.alldocumentsreader.ui.navigation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alldocumentsreader.data.local.FavoriteDocumentEntity
import com.example.alldocumentsreader.data.local.RecentDocumentEntity
import com.example.alldocumentsreader.data.repository.DocumentRepository
import com.example.alldocumentsreader.domain.model.DocCategory
import com.example.alldocumentsreader.domain.model.DocumentItem
import com.example.alldocumentsreader.domain.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppState(
    val language: String = "English",
    val onboardingDone: Boolean = false,
    val folderUri: String? = null,
    val documents: List<DocumentItem> = emptyList(),
    val category: DocCategory = DocCategory.ALL,
    val query: String = "",
    val sort: SortOption = SortOption(),
)

class ReaderViewModel(private val repo: DocumentRepository) : ViewModel() {
    private val category = MutableStateFlow(DocCategory.ALL)
    private val query = MutableStateFlow("")

    val recent: StateFlow<List<RecentDocumentEntity>> = repo.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favorites: StateFlow<List<FavoriteDocumentEntity>> = repo.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val state: StateFlow<AppState> = combine(
        repo.language,
        repo.onboardingDone,
        repo.selectedFolderUri,
        category,
        query,
    ) { lang, done, folder, cat, q ->
        AppState(language = lang, onboardingDone = done, folderUri = folder, category = cat, query = q)
    }.flatMapLatest { base ->
        repo.sortOption(base.category).flatMapLatest { sort ->
            val folder = base.folderUri?.let(Uri::parse)
            val docsFlow = if (folder != null) repo.scanDocuments(folder) else kotlinx.coroutines.flow.flowOf(emptyList())
            combine(docsFlow, repo.filteredDocuments(base.category, base.query, sort)) { _, filtered ->
                base.copy(documents = filtered, sort = sort)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppState())

    fun setLanguage(language: String) = viewModelScope.launch { repo.setLanguage(language) }
    fun completeOnboarding() = viewModelScope.launch { repo.setOnboardingDone(true) }
    fun setCategory(docCategory: DocCategory) { category.value = docCategory }
    fun setQuery(value: String) { query.value = value }
    fun saveSort(option: SortOption) = viewModelScope.launch { repo.saveSort(category.value, option) }

    fun onFolderSelected(uri: Uri) = viewModelScope.launch {
        repo.takePersistablePermission(uri)
        repo.setFolderUri(uri.toString())
    }

    fun openDocument(item: DocumentItem) = viewModelScope.launch { repo.openDocument(item) }
    fun toggleFavorite(item: DocumentItem) = viewModelScope.launch { repo.toggleFavorite(item) }
}

class ReaderViewModelFactory(private val repo: DocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReaderViewModel(repo) as T
        }
        error("Unknown ViewModel")
    }
}
