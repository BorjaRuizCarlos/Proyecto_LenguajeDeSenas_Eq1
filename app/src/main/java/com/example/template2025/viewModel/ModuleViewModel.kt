package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.ModuloResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ================== DATA CLASSES ==================
data class Module(
    val id: Int,
    val title: String,
    val subtitle: String,
    val progress: Float, // Progreso de 0.0 a 1.0
    val unlocked: Boolean,
    val current: Int = 0,
    val max: Int = 0
)

data class Lesson(
    val id: Int,
    val title: String,
    val progress: Int,
    val total: Int
)

// ================== UI STATE ==================
sealed class ModuleUiState {
    object Loading : ModuleUiState()
    data class Success(val modules: List<Module>) : ModuleUiState()
    data class Error(val message: String) : ModuleUiState()
}

// ================== VIEWMODEL ==================
class ModuleViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModuleUiState>(ModuleUiState.Loading)
    val uiState: StateFlow<ModuleUiState> = _uiState

    // Para compatibilidad con el código existente que usa modules directamente
    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    // Un mapa que asocia el ID de un módulo con su lista de lecciones.


    init {
        // Carga los módulos iniciales cuando se crea el ViewModel.
        val defaultModules = listOf(
            Module(1, "Modulo 1", "Fundamentos del lenguaje de señas mexicano", 0.75f, true),
            Module(2, "Modulo 2", "Abecedario y números", 0.2f, true),
            Module(3, "Modulo 3", "Saludos y presentaciones", 0.5f, true),
            Module(4, "Modulo 4", "Preguntas comunes", 0.9f, true)
        )
        _modules.value = defaultModules
        _uiState.value = ModuleUiState.Success(defaultModules)
    }

    /**
     * Llama al endpoint /api/modulos usando el token JWT.
     */
    fun fetchModuloData(token: String?) {
        viewModelScope.launch {
            _uiState.value = ModuleUiState.Loading

            try {
                val authHeader = "Bearer ${token ?: ""}"
                android.util.Log.d("ModuleViewModel", "Llamando a modulos con token=${token?.take(10)}...")
                val response = api.getModuloData(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val body: ModuloResponse = response.body()!!
                    val modules = body.toModuleList()
                    _modules.value = modules
                    _uiState.value = ModuleUiState.Success(modules)
                } else {
                    _uiState.value = ModuleUiState.Error(
                        "Fallo al cargar módulos: " +
                                response.errorBody()?.string().orEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ModuleUiState.Error(
                    "Fallo al cargar módulos: ${e.message}"
                )
            }
        }
    }

    /** Devuelve un módulo específico basado en su ID. */
    fun getModule(moduleId: Int?): Module? {
        return _modules.value.find { it.id == moduleId }
    }

    /** Devuelve la lista de lecciones para un módulo específico. */

}

// ================== MAPEO ModuloResponse -> List<Module> ==================
private fun ModuloResponse.toModuleList(): List<Module> {
    return this.modulos?.map { item ->
        val progress = if (item.max > 0) {
            (item.current.toFloat() / item.max).coerceIn(0f, 1f)
        } else {
            0f
        }

        Module(
            id = item.id,
            title = item.name,
            subtitle = "Progreso: ${item.current}/${item.max}",
            progress = progress,
            unlocked = true,
            current = item.current,
            max = item.max
        )
    } ?: emptyList()
}

