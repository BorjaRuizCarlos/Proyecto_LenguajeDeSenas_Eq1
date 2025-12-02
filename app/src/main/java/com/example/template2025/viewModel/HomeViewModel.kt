package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.HomeResponse
import com.example.template2025.screens.AppData
import com.example.template2025.screens.DayProgress
import com.example.template2025.screens.Mission
import com.example.template2025.screens.Module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ================== UI STATE ==================
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val appData: AppData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

// ================== VIEWMODEL ==================
class HomeViewModel(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    /**
     * Llama al endpoint de home usando el token JWT.
     * Pásale el token que obtienes de DataStore (puede ser null).
     */
    fun fetchHomeData(token: String?) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                // Siempre generamos un String para el header
                // Si no hay token, va vacío (el backend decidirá qué hacer)
                val authHeader = "Bearer ${token ?: ""}"

                val response = api.getHomeData(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val body: HomeResponse = response.body()!!

                    val appData = body.toAppData()

                    _uiState.value = HomeUiState.Success(appData)
                } else {
                    _uiState.value = HomeUiState.Error(
                        "Fallo al cargar datos: " +
                                response.errorBody()?.string().orEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    "Fallo al cargar datos: ${e.message}"
                )
            }
        }
    }
}

// ================== MAPEO HomeResponse -> AppData ==================
private fun HomeResponse.toAppData(): AppData {
    // Claves que esperas en "Dias" (ajusta a las que te regresen)
    val diasOrden: List<String> = listOf("L", "M", "M2", "J", "V", "S", "D")

    // Si Dias viene null, usamos mapa vacío para evitar NPE
    val diasMap: Map<String, Boolean> = this.dias.orEmpty()

    val dailyProgress: List<DayProgress> = diasOrden.map { key ->
        DayProgress(
            day = key,
            completed = diasMap[key] == true
        )
    }

    val dailyMissions: List<Mission> = listOf(
        Mission("Misión 1", this.mision1 ?: 0, 50),
        Mission("Misión 2", this.mision2 ?: 0, 50),
        Mission("Misión 3", this.mision3 ?: 0, 50),
    )

    val generalProgress = Mission(
        name = "Progreso General",
        current = this.progreso ?: 0,
        max = 100
    )

    val lessons: List<Module> = listOf(
        Module("Módulo 1", this.progresoModulo1 ?: 0, 50),
        Module("Módulo 2", this.progresoModulo2 ?: 0, 50),
        Module("Módulo 3", this.progresoModulo3 ?: 0, 50),
    )

    return AppData(
        dailyProgress = dailyProgress,
        dailyMissions = dailyMissions,
        generalProgress = generalProgress,
        streakDays = this.racha ?: 0,
        lessons = lessons
    )
}
