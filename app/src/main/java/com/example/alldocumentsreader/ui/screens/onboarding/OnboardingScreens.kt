package com.example.alldocumentsreader.ui.screens.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LanguageScreen(onNext: (String) -> Unit) {
    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Arabic")
    val selected = remember { mutableStateOf("English") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { onNext(selected.value) }) { Text("Next") }
        }
        Text("Choose language", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(languages) { language ->
                val chosen = selected.value == language
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected.value = language },
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(if (chosen) Color(0xFFFFEBEE) else Color.Transparent)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("🏳️", Modifier.padding(end = 12.dp))
                        Text(language, Modifier.weight(1f))
                        RadioButton(selected = chosen, onClick = { selected.value = language })
                    }
                }
            }
        }
    }
}

@Composable
fun IntroScreen(onNext: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center,
        ) {
            Text("PDF  XLS  PPT  DOC")
        }
        Text("ALL Document Reader", style = MaterialTheme.typography.headlineMedium)
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { Box(Modifier.size(8.dp).background(if (it == 0) Color.Red else Color.LightGray)) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onNext) { Text("Next →") }
        }
    }
}

@Composable
fun PermissionScreen(
    onFolderSelected: (Uri) -> Unit,
    onLater: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) onFolderSelected(uri)
    }
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("WELCOME TO ALL DOCUMENT READER", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("To read your documents please allow All Documents Reader to access your folder where you have files.")
        Spacer(Modifier.height(20.dp))
        Button(onClick = { launcher.launch(null) }) { Text("Allow") }
        TextButton(onClick = onLater) { Text("Later") }
    }
}
