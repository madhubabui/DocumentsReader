package com.example.alldocumentsreader

import android.app.Application
import com.example.alldocumentsreader.data.local.AppDatabase
import com.example.alldocumentsreader.data.preferences.PreferenceManager
import com.example.alldocumentsreader.data.repository.DocumentRepository

class ReaderApp : Application() {
    lateinit var repository: DocumentRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        val prefs = PreferenceManager(this)
        repository = DocumentRepository(
            context = this,
            recentDao = db.recentDao(),
            favoriteDao = db.favoriteDao(),
            prefs = prefs,
        )
    }
}
