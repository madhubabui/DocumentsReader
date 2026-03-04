package com.example.alldocumentsreader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alldocumentsreader.domain.model.DocumentItem

@Composable
fun DocumentItemRow(
    item: DocumentItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.Description, contentDescription = null)
        Column(Modifier.weight(1f)) {
            Text(item.displayName, style = MaterialTheme.typography.titleSmall)
            Text("${item.extension.uppercase()} • ${item.size} bytes", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                tint = if (item.isFavorite) Color(0xFFFFD54F) else MaterialTheme.colorScheme.onSurface,
                contentDescription = "Favorite",
            )
        }
        IconButton(onClick = { expanded.value = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            listOf("Rename", "Share", "Delete", "Details").forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = { expanded.value = false })
            }
        }
    }
}
