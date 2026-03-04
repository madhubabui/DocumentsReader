package com.example.alldocumentsreader.ui.screens.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ViewerScreen(
    fileUri: String,
    mimeType: String,
    displayName: String,
    fileType: String,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ViewerTopBar(fileType = fileType, title = displayName, onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("In-app document renderer placeholder", style = MaterialTheme.typography.titleMedium)
            Text("URI: $fileUri")
            Text("MIME: $mimeType")
            repeat(20) { Text("Content line ${it + 1} for $displayName") }
        }
        ViewerBottomBar(fileType)
    }
}

@Composable
private fun ViewerTopBar(fileType: String, title: String, onBack: () -> Unit) {
    val actions = when (fileType.lowercase()) {
        "pdf" -> listOf("PDF", "Rotate", "Brightness", "Search", "Share")
        "doc", "docx" -> listOf("Rotate", "Search", "Dark")
        "epub" -> listOf("Search", "Share", "Dark")
        "ppt", "pptx" -> listOf("Rotate", "Search", "Dark", "⋮")
        "txt" -> listOf("Rotate", "Search", "Dark", "⋮")
        "xls", "xlsx" -> listOf("Rotate", "Search", "Dark", "⋮")
        else -> listOf("Search")
    }
    Row(
        Modifier.fillMaxWidth().background(Color(0xFFEEEEEE)).padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(onClick = onBack) { Text("Back") }
        Text(title, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
        Text(actions.joinToString(" | "))
    }
}

@Composable
private fun ViewerBottomBar(fileType: String) {
    val toolbar = listOf("Mobile View", "Go to Page", "Export PDF", "Share")
    Column(Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(10.dp)) {
        when (fileType.lowercase()) {
            "pdf" -> Text("Page thumbnail strip")
            "ppt", "pptx" -> Text("Slide thumbnails + banner ad placeholder")
            "txt", "xls", "xlsx", "doc", "docx" -> Text(toolbar.joinToString(" | "))
            "epub" -> Text("Font size | Font family | Line spacing | Light/Sepia/Dark")
        }
    }
}
