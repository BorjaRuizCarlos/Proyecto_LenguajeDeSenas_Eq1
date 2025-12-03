package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.HomeResponse
import com.example.template2025.data.api.MissionRemote
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
     * Llama al endpoint de home y al endpoint de misiones diarias
     * usando el token JWT (puede ser null).
     */
    fun fetchHomeData(token: String?) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val authHeader = "Bearer ${token ?: ""}"

                // 1) Home
                val homeResponse = api.getHomeData(authHeader)

                // 2) Misiones diarias
                val dailyResponse = api.getDailyMissions(authHeader)

                if (homeResponse.isSuccessful && homeResponse.body() != null &&
                    dailyResponse.isSuccessful && dailyResponse.body() != null
                ) {
                    val homeBody: HomeResponse = homeResponse.body()!!
                    val remoteMissions: List<MissionRemote> =
                        dailyResponse.body()!!.misiones

                    // Construimos AppData mezclando Home + Daily Missions
                    val appData = homeBody.toAppData(remoteMissions)

                    _uiState.value = HomeUiState.Success(appData)

                } else {
                    val homeError = homeResponse.errorBody()?.string().orEmpty()
                    val dailyError = dailyResponse.errorBody()?.string().orEmpty()

                    _uiState.value = HomeUiState.Error(
                        "Fallo al cargar datos.\n" +
                                "Home: $homeError\n" +
                                "Misiones: $dailyError"
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

// ================== MAPEOS A MODELOS DE UI ==================

/**
 * Mapea la respuesta de /home + la lista de misiones diarias
 * al modelo AppData que usa tu UI.
 */
private fun HomeResponse.toAppData(
    remoteMissions: List<MissionRemote>
): AppData {

    // 🔥 CLAVES DEL BACKEND → LETRA QUE MOSTRARÁS EN LA UI
    val diasOrden: List<Pair<String, String>> = listOf(
        "Lunes" to "L",
        "Martes" to "M",
        "Miercoles" to "Mi",
        "Jueves" to "J",
        "Viernes" to "V",
        "Sabado" to "S",
        "Domingo" to "D"
    )

    // Mapa real del backend
    val diasMap: Map<String, Boolean> = this.dias.orEmpty()

    // Aquí se corrige el completed para que coincida con el backend
    val dailyProgress: List<DayProgress> = diasOrden.map { (backendKey, uiLabel) ->
        DayProgress(
            day = uiLabel,
            completed = diasMap[backendKey] == true
        )
    }

    // 🔹 Misiones traídas desde /missions/daily
    val dailyMissions: List<Mission> = remoteMissions.map { it.toUiMission() }

    // Progreso general (viene como Double)
    val progresoGeneralInt: Int = ((this.progreso ?: 0.0)
        .coerceIn(0.0, 100.0))
        .toInt()

    val generalProgress = Mission(
        name = "Progreso General",
        current = progresoGeneralInt,
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

/**
 * Mapea una MissionRemote (del backend) a tu modelo de UI Mission.
 */
private fun MissionRemote.toUiMission(): Mission {
    return Mission(
        name = this.nombre,
        current = this.progreso_actual,
        max = this.meta
    )
}
