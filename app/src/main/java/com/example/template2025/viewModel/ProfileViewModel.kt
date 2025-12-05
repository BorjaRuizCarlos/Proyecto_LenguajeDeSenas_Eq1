package com.example.template2025.viewModel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.UpdateProfileRequest
import com.example.template2025.dataStore.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Data class que representa la respuesta de la API /profile/me
data class UserProfile(
    val id_usuario: Int,
    val nombre: String,
    val correo: String,
    val monedas: Int,
    val es_admin: Boolean,
    val creado_en: String
)

// Clase sellada para el estado de la UI
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val userProfile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(private val apiService: ApiService, private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _bio = mutableStateOf("¡Apasionado por el lenguaje de señas y el aprendizaje continuo!")
    val bio: State<String> = _bio

    private val _selectedAvatarResId = mutableStateOf(R.drawable.ic_character_completed) // Valor por defecto
    val selectedAvatarResId: State<Int> = _selectedAvatarResId

    init {
        viewModelScope.launch {
            val savedAvatarId = TokenStore.avatarIdFlow(context).firstOrNull()
            if (savedAvatarId != null && savedAvatarId != 0) { // Check for default value
                _selectedAvatarResId.value = savedAvatarId
            }
        }
    }

    fun fetchProfileData(token: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val authHeader = "Bearer $token"
                val response = apiService.getProfile(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val userProfile = response.body()!!
                    _uiState.value = ProfileUiState.Success(userProfile)
                    _username.value = userProfile.nombre
                    _email.value = userProfile.correo
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _uiState.value = ProfileUiState.Error("Error: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("No se pudo conectar al servidor: ${e.message}")
            }
        }
    }

    fun updateAvatar(newAvatarResId: Int) {
        viewModelScope.launch {
            _selectedAvatarResId.value = newAvatarResId
            TokenStore.saveAvatarId(context, newAvatarResId)
        }
    }

    // Función para actualizar nombre y bio
    fun updateProfile(token: String, newName: String, newBio: String) {
        // Actualizamos la bio localmente
        _bio.value = newBio
        
        // Llamamos a la API para actualizar el nombre
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                val requestBody = UpdateProfileRequest(nombre = newName)
                val response = apiService.updateProfile(authHeader, requestBody)

                if (response.isSuccessful && response.body() != null) {
                    _username.value = response.body()!!.nombre
                } else {
                    println("Error al actualizar el nombre: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción al actualizar el nombre: ${e.message}")
            }
        }
    }
}

class ProfileViewModelFactory(
    private val apiService: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
