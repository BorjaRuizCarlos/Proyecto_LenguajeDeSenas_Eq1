package com.example.template2025.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Data class que representa la respuesta de la API /profile/me
// Basado en el JSON que me proporcionaste
data class UserProfile(
    val id_usuario: Int,
    val nombre: String,
    val correo: String,
    val monedas: Int,
    val es_admin: Boolean,
    val creado_en: String
)

// 2. Clase sellada para el estado de la UI
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val userProfile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

// 3. El ViewModel
class ProfileViewModel(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Estos son estados locales que se actualizan cuando la API responde
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    // La biografía no viene de la API, así que la manejamos localmente
    private val _bio = mutableStateOf("¡Apasionado por el lenguaje de señas y el aprendizaje continuo!")
    val bio: State<String> = _bio

    // El avatar no viene de la API, así que usaremos uno por defecto
    private val _selectedAvatarResId = mutableStateOf(R.drawable.ic_character_completed)
    val selectedAvatarResId: State<Int> = _selectedAvatarResId

    /**
     * Llama al endpoint /profile/me y actualiza el estado de la UI.
     */
    fun fetchProfileData(token: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // La API espera un token de tipo "Bearer", lo formateamos
                val authHeader = "Bearer $token"
                val response = apiService.getProfile(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val userProfile = response.body()!!
                    _uiState.value = ProfileUiState.Success(userProfile)

                    // Actualizamos los estados de la UI con los datos recibidos
                    _username.value = userProfile.nombre

                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido en la respuesta"
                    _uiState.value = ProfileUiState.Error("Error: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("No se pudo conectar al servidor: ${e.message}")
            }
        }
    }

    /**
     * Función "hardcodeada" para actualizar el avatar localmente.
     */
    fun updateAvatar(newAvatarResId: Int) {
        _selectedAvatarResId.value = newAvatarResId
    }

    // Aquí podría ir en el futuro una función para actualizar el perfil
    // fun updateProfileName(token: String, newName: String) { ... }
}

// 4. La Factory para el ViewModel
class ProfileViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
