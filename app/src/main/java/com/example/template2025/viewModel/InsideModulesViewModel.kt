package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.LeccionesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ================== UI STATE ==================
sealed class InsideModulesUiState {
    object Loading : InsideModulesUiState()
    data class Success(
        val moduloId: Int,
        val moduloNombre: String,
        val lecciones: List<Lesson>
    ) : InsideModulesUiState()
    data class Error(val message: String) : InsideModulesUiState()
}

// ================== VIEWMODEL ==================
class InsideModulesViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsideModulesUiState>(InsideModulesUiState.Loading)
    val uiState: StateFlow<InsideModulesUiState> = _uiState

    /**
     * Llama al endpoint /api/modulos/{modulo_id}/lecciones/ usando el token JWT.
     */
    fun fetchLeccionesData(token: String?, moduloId: Int) {
        viewModelScope.launch {
            _uiState.value = InsideModulesUiState.Loading

            try {
                android.util.Log.d("InsideModulesViewModel", "========== INICIANDO LLAMADA A LECCIONES ==========")
                android.util.Log.d("InsideModulesViewModel", "moduleId=$moduloId")
                android.util.Log.d("InsideModulesViewModel", "token=${token?.take(20)}...")
                val authHeader = "Bearer ${token ?: ""}"
                android.util.Log.d("InsideModulesViewModel", "authHeader='${authHeader.take(30)}...'")
                android.util.Log.d("InsideModulesViewModel", "Llamando: api.getLeccionesData(authHeader, $moduloId)")
                val response = api.getLeccionesData(authHeader, moduloId)
                android.util.Log.d("InsideModulesViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val body: LeccionesResponse = response.body()!!
                    val lecciones = body.toLessonList()

                    _uiState.value = InsideModulesUiState.Success(
                        moduloId = body.moduloId,
                        moduloNombre = body.moduloNombre,
                        lecciones = lecciones
                    )
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    android.util.Log.d("InsideModulesViewModel", "ERROR RESPONSE BODY: $errorBody")
                    _uiState.value = InsideModulesUiState.Error(
                        "Fallo al cargar lecciones (${response.code()}): $errorBody"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = InsideModulesUiState.Error(
                    "Fallo al cargar lecciones: ${e.message}"
                )
            }
        }
    }
}

// ================== MAPEO LeccionesResponse -> List<Lesson> ==================
private fun LeccionesResponse.toLessonList(): List<Lesson> {
    return this.lecciones?.map { item ->
        Lesson(
            id = item.id,
            title = item.name,
            progress = item.current,
            total = item.max
        )
    } ?: emptyList()
}
