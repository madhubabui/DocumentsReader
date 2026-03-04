package com.example.alldocumentsreader.data.saf

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.alldocumentsreader.domain.model.DocumentItem

class SafScanner(private val context: Context) {

    private val supported = setOf("pdf", "doc", "docx", "epub", "xls", "xlsx", "ppt", "pptx", "txt")

    fun scan(folderUri: Uri, recursive: Boolean = true, maxFiles: Int = 5000): List<DocumentItem> {
        val root = DocumentFile.fromTreeUri(context, folderUri) ?: return emptyList()
        val out = mutableListOf<DocumentItem>()
        walk(root, recursive, out, maxFiles)
        return out
    }

    private fun walk(node: DocumentFile, recursive: Boolean, out: MutableList<DocumentItem>, maxFiles: Int) {
        if (!node.isDirectory) return
        node.listFiles().forEach { file ->
            if (out.size >= maxFiles) return
            if (file.isDirectory && recursive) {
                walk(file, recursive, out, maxFiles)
            } else if (file.isFile) {
                val name = file.name ?: return@forEach
                val ext = name.substringAfterLast('.', "").lowercase()
                if (ext in supported) {
                    out += DocumentItem(
                        uri = file.uri,
                        displayName = name,
                        mimeType = file.type ?: "application/octet-stream",
                        extension = ext,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        isFavorite = false,
                    )
                }
            }
        }
    }
}
