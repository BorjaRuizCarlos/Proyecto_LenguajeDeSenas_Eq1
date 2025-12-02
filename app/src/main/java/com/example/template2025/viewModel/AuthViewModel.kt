package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.AuthRepository
import com.example.template2025.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Para SIGNUP
data class SignupUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

// Para LOGIN (con accessToken)
data class LoginUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val accessToken: String? = null
)

class AuthViewModel : ViewModel() {

    private val api = ApiService.create("https://androidbackend-production-1dbe.up.railway.app/")
    private val repo = AuthRepository(api)

    private val _signup = MutableStateFlow(SignupUiState())
    val signup: StateFlow<SignupUiState> = _signup

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    fun resetSignup() { _signup.value = SignupUiState() }
    fun resetLogin() { _login.value = LoginUiState() }

    fun signup(nombre: String, correo: String, pass: String) = viewModelScope.launch {
        _signup.value = SignupUiState(loading = true)
        val result = repo.signup(nombre, correo, pass)
        _signup.value = result.fold(
            onSuccess = { SignupUiState(success = true) },
            onFailure = { SignupUiState(error = it.message ?: "Error inesperado") }
        )
    }

    fun login(correo: String, pass: String) = viewModelScope.launch {
        _login.value = LoginUiState(loading = true)

        val result = repo.login(correo, pass)
        _login.value = result.fold(
            onSuccess = { response ->
                // 👇 AQUÍ usamos response.token (del JSON { "token": "..." })
                LoginUiState(
                    success = true,
                    accessToken = response.token
                )
            },
            onFailure = { e ->
                LoginUiState(error = e.message ?: "Error inesperado")
            }
        )
    }

}
