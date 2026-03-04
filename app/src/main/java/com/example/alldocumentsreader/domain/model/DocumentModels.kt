package com.example.alldocumentsreader.domain.model

import android.net.Uri

enum class DocCategory { ALL, PDF, WORD, EBOOK, EXCEL, PPT, TXT }
enum class SortField { TITLE, TYPE, DATE, SIZE }
enum class SortOrder { ASC, DESC }

data class SortOption(val field: SortField = SortField.DATE, val order: SortOrder = SortOrder.DESC)

data class DocumentItem(
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val extension: String,
    val size: Long,
    val lastModified: Long,
    val isFavorite: Boolean,
)

fun DocCategory.matches(ext: String): Boolean = when (this) {
    DocCategory.ALL -> true
    DocCategory.PDF -> ext == "pdf"
    DocCategory.WORD -> ext == "doc" || ext == "docx"
    DocCategory.EBOOK -> ext == "epub"
    DocCategory.EXCEL -> ext == "xls" || ext == "xlsx"
    DocCategory.PPT -> ext == "ppt" || ext == "pptx"
    DocCategory.TXT -> ext == "txt"
}
