package com.example.alldocumentsreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alldocumentsreader.ui.navigation.ReaderNavGraph
import com.example.alldocumentsreader.ui.navigation.ReaderViewModel
import com.example.alldocumentsreader.ui.navigation.ReaderViewModelFactory
import com.example.alldocumentsreader.ui.theme.AllDocumentsReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReaderAppContent()
        }
    }
}

@Composable
private fun ReaderAppContent() {
    val app = LocalContext.current.applicationContext as ReaderApp
    val vm: ReaderViewModel = viewModel(factory = ReaderViewModelFactory(app.repository))
    AllDocumentsReaderTheme {
        Surface {
            ReaderNavGraph(vm)
        }
    }
}
