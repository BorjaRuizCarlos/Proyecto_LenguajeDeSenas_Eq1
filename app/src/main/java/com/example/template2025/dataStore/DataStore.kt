// app/src/main/java/com/example/template2025/dataStore/DataStore.kt
package com.example.template2025.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ÚNICA extensión llamada dataStore
val Context.dataStore by preferencesDataStore(name = "app_prefs")

object DataStore {
    private val KEY_LOGGED = booleanPreferencesKey("logged_in")

    fun isLoggedIn(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_LOGGED] ?: false }

    suspend fun setLoggedIn(context: Context, value: Boolean) {
        context.dataStore.edit { it[KEY_LOGGED] = value }
    }
}
