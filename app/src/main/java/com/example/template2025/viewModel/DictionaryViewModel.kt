package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.DictionaryListResponse
import com.example.template2025.data.api.DictionaryWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ----- UI STATE LISTA -----
data class DictionaryUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val words: List<DictionaryWord> = emptyList()
)

// ----- VIEWMODEL LISTA -----
class DictionaryViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DictionaryUiState(loading = true))
    val uiState: StateFlow<DictionaryUiState> = _uiState

    /**
     * Carga el diccionario usando el token JWT.
     */
    fun loadDictionary(token: String?) {
        viewModelScope.launch {
            _uiState.value = DictionaryUiState(loading = true)

            // Si no hay token, marcamos error directamente
            if (token.isNullOrBlank()) {
                _uiState.value = DictionaryUiState(
                    loading = false,
                    error = "No se encontró token. Vuelve a iniciar sesión."
                )
                return@launch
            }

            try {
                val authHeader = "Bearer $token"

                // IMPORTANTE: ApiService.getDictionary ahora debe aceptar el header
                val res = api.getDictionary(authHeader)   // sin search, trae todo

                if (res.isSuccessful && res.body() != null) {
                    val body: DictionaryListResponse = res.body()!!
                    _uiState.value = DictionaryUiState(
                        loading = false,
                        words = body.palabras
                    )
                } else {
                    _uiState.value = DictionaryUiState(
                        loading = false,
                        error = res.errorBody()?.string()
                            .orEmpty()
                            .ifBlank { "Error al cargar diccionario" }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DictionaryUiState(
                    loading = false,
                    error = e.message ?: "Error inesperado"
                )
            }
        }
    }
}

// ----- FACTORY LISTA -----
class DictionaryViewModelFactory(
    private val api: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DictionaryViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
