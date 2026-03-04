package com.example.alldocumentsreader.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.alldocumentsreader.domain.model.DocCategory
import com.example.alldocumentsreader.domain.model.SortField
import com.example.alldocumentsreader.domain.model.SortOption
import com.example.alldocumentsreader.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("reader_prefs")

class PreferenceManager(private val context: Context) {
    private object Keys {
        val language = stringPreferencesKey("language")
        val onboardingDone = booleanPreferencesKey("onboarding_done")
        val selectedFolderUri = stringPreferencesKey("selected_folder_uri")
    }

    val language: Flow<String> = context.dataStore.data.map { it[Keys.language] ?: "English" }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.onboardingDone] ?: false }
    val selectedFolderUri: Flow<String?> = context.dataStore.data.map { it[Keys.selectedFolderUri] }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.language] = language }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.onboardingDone] = done }
    }

    suspend fun setFolderUri(uri: String) {
        context.dataStore.edit { it[Keys.selectedFolderUri] = uri }
    }

    private fun sortFieldKey(category: DocCategory) = stringPreferencesKey("sort_field_${category.name}")
    private fun sortOrderKey(category: DocCategory) = stringPreferencesKey("sort_order_${category.name}")

    fun sortOption(category: DocCategory): Flow<SortOption> = context.dataStore.data.map { pref: Preferences ->
        val field = pref[sortFieldKey(category)]?.let { SortField.valueOf(it) } ?: SortField.DATE
        val order = pref[sortOrderKey(category)]?.let { SortOrder.valueOf(it) } ?: SortOrder.DESC
        SortOption(field, order)
    }

    suspend fun saveSort(category: DocCategory, sortOption: SortOption) {
        context.dataStore.edit {
            it[sortFieldKey(category)] = sortOption.field.name
            it[sortOrderKey(category)] = sortOption.order.name
        }
    }
}
