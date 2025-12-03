package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.LessonContentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ================== UI STATE ==================
sealed class LessonContentUiState {
    object Loading : LessonContentUiState()
    data class Success(
        val idLeccion: Int,
        val titulo: String,
        val idModulo: Int,
        val orden: Int,
        val completado: Boolean,
        val videos: List<VideoData>
    ) : LessonContentUiState()
    data class Error(val message: String) : LessonContentUiState()
}

data class VideoData(
    val id: Int = 0,
    val nombre: String = "",
    val url: String = "",
    val ordenVideo: Int = 0,
    val additionalProp1: Any? = null
)

// ================== VIEWMODEL ==================
class LessonContentViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonContentUiState>(LessonContentUiState.Loading)
    val uiState: StateFlow<LessonContentUiState> = _uiState

    /**
     * Llama al endpoint /api/lessons/{leccion_id} usando el token JWT.
     */
    fun fetchLessonContent(token: String?, leccionId: Int) {
        viewModelScope.launch {
            _uiState.value = LessonContentUiState.Loading

            try {
                android.util.Log.d("LessonContentViewModel", "========== INICIANDO LLAMADA A LESSON CONTENT ==========")
                android.util.Log.d("LessonContentViewModel", "leccionId=$leccionId")
                android.util.Log.d("LessonContentViewModel", "token=${token?.take(20)}...")
                val authHeader = "Bearer ${token ?: ""}"
                android.util.Log.d("LessonContentViewModel", "authHeader='${authHeader.take(30)}...'")
                android.util.Log.d("LessonContentViewModel", "Llamando: api.getLessonContent(authHeader, $leccionId)")
                android.util.Log.d("LessonContentViewModel", "URL esperada: /api/lessons/$leccionId")

                val response = api.getLessonContent(authHeader, leccionId)
                android.util.Log.d("LessonContentViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                android.util.Log.d("LessonContentViewModel", "Response message: ${response.message()}")

                if (response.isSuccessful && response.body() != null) {
                    val body: LessonContentResponse = response.body()!!
                    android.util.Log.d("LessonContentViewModel", "========== RESPUESTA DEL API ==========")
                    android.util.Log.d("LessonContentViewModel", "titulo: ${body.titulo}")
                    android.util.Log.d("LessonContentViewModel", "videos count: ${body.videos?.size}")
                    body.videos?.forEachIndexed { index, video ->
                        android.util.Log.d("LessonContentViewModel", "Video $index: id_video=${video.id}, titulo='${video.nombre}', url='${video.url}', orden=${video.orden}")
                    }                    // Ordenar videos por ID (menor a mayor)
                    val videos = body.videos
                        ?.sortedBy { it.id }
                        ?.map { item ->
                            VideoData(
                                id = item.id,
                                nombre = item.nombre,
                                url = item.url,
                                ordenVideo = item.orden

                            )
                        } ?: emptyList()

                    _uiState.value = LessonContentUiState.Success(
                        idLeccion = body.idLeccion,
                        titulo = body.titulo,
                        idModulo = body.idModulo,
                        orden = body.orden,
                        completado = body.completado,
                        videos = videos
                    )
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    android.util.Log.d("LessonContentViewModel", "ERROR RESPONSE BODY: $errorBody")
                    _uiState.value = LessonContentUiState.Error(
                        "Fallo al cargar contenido de lección (${response.code()}): $errorBody"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LessonContentUiState.Error(
                    "Fallo al cargar contenido de lección: ${e.message}"
                )
            }
        }
    }
}
