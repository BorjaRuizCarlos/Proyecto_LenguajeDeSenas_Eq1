// app/src/main/java/com/example/template2025/viewModel/AppViewModel.kt
package com.example.template2025.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.DataStore   // ✅ éste es tu singleton real
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false
)

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    init {
        observeLoginFlag()
    }

    private fun observeLoginFlag() {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            // Flujo booleano desde DataStore
            DataStore.isLoggedIn(ctx).collect { logged ->
                _auth.value = AuthState(isLoading = false, isLoggedIn = logged)
            }
        }
    }

    fun login() {
        val ctx = getApplication<Application>()
        viewModelScope.launch { DataStore.setLoggedIn(ctx, true) }
    }

    fun logout() {
        val ctx = getApplication<Application>()
        viewModelScope.launch { DataStore.setLoggedIn(ctx, false) }
    }
}
