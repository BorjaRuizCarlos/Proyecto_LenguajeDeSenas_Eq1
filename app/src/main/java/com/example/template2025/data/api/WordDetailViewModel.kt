package com.example.template2025.data.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ----- UI STATE DETALLE -----
data class WordDetailUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val word: DictionaryWordDetail? = null
)

// ----- VIEWMODEL DETALLE -----
class WordDetailViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordDetailUiState(loading = true))
    val uiState: StateFlow<WordDetailUiState> = _uiState

    fun loadWord(id: Int) {
        viewModelScope.launch {
            _uiState.value = WordDetailUiState(loading = true)

            try {
                val res = api.getDictionaryWord(id)
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = WordDetailUiState(
                        loading = false,
                        word = res.body()
                    )
                } else {
                    _uiState.value = WordDetailUiState(
                        loading = false,
                        error = res.errorBody()?.string()
                            .orEmpty()
                            .ifBlank { "Error al cargar palabra" }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = WordDetailUiState(
                    loading = false,
                    error = e.message ?: "Error inesperado"
                )
            }
        }
    }
}

// ----- FACTORY DETALLE -----
class WordDetailViewModelFactory(
    private val api: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordDetailViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
