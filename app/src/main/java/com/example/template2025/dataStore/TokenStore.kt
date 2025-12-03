// app/src/main/java/com/example/template2025/dataStore/TokenStore.kt
package com.example.template2025.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 👇 nombre distinto para evitar choque con DataStore.kt
private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

object TokenStore {
    private val KEY_TOKEN = stringPreferencesKey("jwt_token")
    private val KEY_AVATAR = intPreferencesKey("user_avatar_id") // 🔹 Nueva clave para el avatar

    // --- Token --- 
    fun tokenFlow(context: Context): Flow<String?> =
        context.authDataStore.data.map { it[KEY_TOKEN] }

    suspend fun saveToken(context: Context, token: String) {
        context.authDataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun clearToken(context: Context) {
        context.authDataStore.edit { it.remove(KEY_TOKEN) }
    }

    // --- Avatar --- (Nuevas funciones)
    fun avatarIdFlow(context: Context): Flow<Int?> =
        context.authDataStore.data.map { it[KEY_AVATAR] }

    suspend fun saveAvatarId(context: Context, avatarId: Int) {
        context.authDataStore.edit { it[KEY_AVATAR] = avatarId }
    }
    
    // --- Limpieza general ---
    suspend fun clearAll(context: Context) {
        context.authDataStore.edit { it.clear() }
    }
}
