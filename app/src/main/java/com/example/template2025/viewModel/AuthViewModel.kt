package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.AuthRepository
import com.example.template2025.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    // Ajusta aquí tu base URL local:
    private val api = ApiService.create("https://androidbackend-production-1dbe.up.railway.app/")
    private val repo = AuthRepository(api)

    private val _signup = MutableStateFlow(UiState())
    val signup: StateFlow<UiState> = _signup

    private val _login = MutableStateFlow(UiState())
    val login: StateFlow<UiState> = _login

    fun resetSignup() { _signup.value = UiState() }
    fun resetLogin() { _login.value = UiState() }

    fun signup(nombre: String, correo: String, pass: String) = viewModelScope.launch {
        _signup.value = UiState(loading = true)
        val result = repo.signup(nombre, correo, pass)
        _signup.value = result.fold(
            onSuccess = { UiState(success = true) },
            onFailure = { UiState(error = it.message ?: "Error inesperado") }
        )
    }

    fun login(correo: String, pass: String) = viewModelScope.launch {
        _login.value = UiState(loading = true)
        val result = repo.login(correo, pass)
        _login.value = result.fold(
            onSuccess = { UiState(success = true) },
            onFailure = { UiState(error = it.message ?: "Error inesperado") }
        )
    }
}

