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

// Estado de la UI para manejar carga, éxito o error
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val appData: AppData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Mapeo de HomeResponse (API) → AppData (UI)
    private fun mapToAppData(response: HomeResponse): AppData {
        return AppData(
            dailyProgress = response.dailyProgress.map { DayProgress(it.day, it.completed) },
            dailyMissions = response.dailyMissions.map { Mission(it.name, it.current, it.max) },
            generalProgress = Mission(
                response.generalProgress.name,
                response.generalProgress.current,
                response.generalProgress.max
            ),
            streakDays = response.streakDays,
            lessons = response.lessons.map { Module(it.name, it.current, it.max) }
        )
    }

    /**
     * Llama al endpoint /api/home usando el token del usuario.
     */
    fun fetchHomeData(token: String) {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                val response: HomeResponse = apiService.getHomeData(authHeader)
                val appData = mapToAppData(response)
                _uiState.value = HomeUiState.Success(appData)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    "Fallo al cargar datos: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }
}
