package com.example.alldocumentsreader.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alldocumentsreader.domain.model.DocCategory
import com.example.alldocumentsreader.domain.model.DocumentItem
import com.example.alldocumentsreader.ui.components.DocumentItemRow

private fun tabColor(category: DocCategory): Color = when (category) {
    DocCategory.ALL -> Color.White
    DocCategory.PDF -> Color.Red
    DocCategory.WORD -> Color(0xFF1E88E5)
    DocCategory.EBOOK -> Color(0xFFFDD835)
    DocCategory.EXCEL -> Color(0xFF43A047)
    DocCategory.PPT -> Color(0xFFFB8C00)
    DocCategory.TXT -> Color.Gray
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRootScreen(
    folderUri: String?,
    category: DocCategory,
    documents: List<DocumentItem>,
    onCategoryChange: (DocCategory) -> Unit,
    onFolderSelected: (Uri) -> Unit,
    onOpenDocument: (DocumentItem) -> Unit,
    onToggleFavorite: (DocumentItem) -> Unit,
) {
    val selectedBottom = remember { mutableIntStateOf(0) }
    val categories = DocCategory.entries
    val pager = rememberPagerState(initialPage = category.ordinal, pageCount = { categories.size })
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) onFolderSelected(uri)
    }

    LaunchedEffect(pager.currentPage) { onCategoryChange(categories[pager.currentPage]) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("All Document Reader", modifier = Modifier.padding(16.dp))
                listOf("Home", "Settings", "Rate Us", "Share App").forEach {
                    NavigationDrawerItem(label = { Text(it) }, selected = false, onClick = {})
                }
                Spacer(Modifier.weight(1f))
                Text("v1.6.6", modifier = Modifier.padding(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("All Document Reader") },
                    navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
                    actions = { IconButton(onClick = {}) { Icon(Icons.Default.Search, null) } },
                    modifier = Modifier.background(tabColor(category)),
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "Recent", "Favorite").forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedBottom.intValue == index,
                            onClick = { selectedBottom.intValue = index },
                            icon = {},
                            label = { Text(label) },
                        )
                    }
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Folder: ${folderUri ?: "Not selected"}", modifier = Modifier.weight(1f))
                    TextButton(onClick = { folderPicker.launch(null) }) { Text("Change") }
                }
                Row(Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach {
                        Text(
                            it.name,
                            modifier = Modifier
                                .background(if (it == category) tabColor(it).copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onCategoryChange(it) }
                                .padding(8.dp),
                        )
                    }
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${documents.size} Documents")
                    Text("Sort by ⏷")
                }

                HorizontalPager(state = pager, modifier = Modifier.weight(1f)) {
                    LazyColumn {
                        items(documents, key = { it.uri.toString() }) { item ->
                            DocumentItemRow(item = item, onClick = { onOpenDocument(item) }, onToggleFavorite = { onToggleFavorite(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentScreen(items: List<String>, onOpen: (String) -> Unit) {
    LazyColumn { items(items) { Text(it, Modifier.fillMaxWidth().clickable { onOpen(it) }.padding(12.dp)) } }
}

@Composable
fun FavoriteScreen(items: List<String>, onOpen: (String) -> Unit) {
    LazyColumn { items(items) { Text(it, Modifier.fillMaxWidth().clickable { onOpen(it) }.padding(12.dp)) } }
}
