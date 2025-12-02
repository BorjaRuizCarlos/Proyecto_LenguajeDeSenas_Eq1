package com.example.template2025.composables

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.template2025.dataStore.TokenStore
import com.example.template2025.dataStore.DataStore
import kotlinx.coroutines.flow.Flow

// 1. CompositionLocal para el token (ÚNICO lugar donde se define)
val LocalAuthToken = staticCompositionLocalOf<String?> { null }

// 2. Optional: flow para saber si está loggeado
fun isLoggedInFlow(context: Context): Flow<Boolean> =
    DataStore.isLoggedIn(context)

// 3. Raíz que expone el token (úsala en MainActivity / Root)
@Composable
fun AuthProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Leemos el token guardado en DataStore
    val tokenFlow = remember { TokenStore.tokenFlow(context) }
    val token by tokenFlow.collectAsState(initial = null)

    CompositionLocalProvider(
        LocalAuthToken provides token
    ) {
        content()
    }
}
