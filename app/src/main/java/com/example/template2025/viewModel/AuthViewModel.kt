package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.AuthRepository
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.SignupResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignupUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val data: SignupResponse? = null
)

class AuthViewModel : ViewModel() {

    // Emulador -> backend local
    private val api: ApiService = ApiService.create("http://10.0.2.2:8000/")
    private val repo: AuthRepository = AuthRepository(api)

    private val _signup: MutableStateFlow<SignupUiState> = MutableStateFlow(SignupUiState())
    val signup: StateFlow<SignupUiState> = _signup

    fun signup(nombre: String, correo: String, contrasena: String) {
        _signup.value = SignupUiState(loading = true)
        viewModelScope.launch {
            val result = repo.signup(nombre, correo, contrasena)
            _signup.value = result.fold(
                onSuccess = { SignupUiState(success = true, data = it) },
                onFailure = { SignupUiState(error = it.message ?: "Error desconocido") }
            )
        }
    }

    // Evitamos colisión con ViewModel.clear()
    fun resetSignup() {
        _signup.value = SignupUiState()
    }
}
