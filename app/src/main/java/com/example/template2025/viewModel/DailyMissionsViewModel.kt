// DailyMissionsViewModel.kt
package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.MissionRemote
import com.example.template2025.data.api.UpdateMissionRequest
import com.example.template2025.ui.theme.MissionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ------------------ UI STATE ------------------

sealed class DailyMissionsUiState {
    object Loading : DailyMissionsUiState()
    data class Success(val missions: List<MissionUi>) : DailyMissionsUiState()
    data class Error(val message: String) : DailyMissionsUiState()
}

// ------------------ VIEWMODEL ------------------

class DailyMissionsViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DailyMissionsUiState>(DailyMissionsUiState.Loading)
    val uiState: StateFlow<DailyMissionsUiState> = _uiState

    fun fetchDailyMissions(token: String) {
        viewModelScope.launch {
            _uiState.value = DailyMissionsUiState.Loading
            try {
                val response = apiService.getDailyMissions("Bearer $token")

                if (!response.isSuccessful) {
                    _uiState.value = DailyMissionsUiState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _uiState.value = DailyMissionsUiState.Error(
                        "Respuesta vacía del servidor."
                    )
                    return@launch
                }

                val missionsUi = body.misiones.map { dto ->
                    mapMissionToUi(dto)
                }

                _uiState.value = DailyMissionsUiState.Success(missionsUi)
            } catch (e: Exception) {
                _uiState.value = DailyMissionsUiState.Error(
                    e.message ?: "Error al cargar misiones diarias."
                )
            }
        }
    }

    fun updateMissionProgress(token: String, missionId: Int, progreso: Int) {
        viewModelScope.launch {
            try {
                apiService.updateMission(
                    authorization = "Bearer $token",
                    body = UpdateMissionRequest(
                        mision_id = missionId,
                        progreso = progreso
                    )
                )
                // Si quieres refrescar después:
                fetchDailyMissions(token)
            } catch (_: Exception) {
                // podrías manejar un error aquí si hace falta
            }
        }
    }

    // ---------- Helpers ----------

    private fun mapMissionToUi(dto: MissionRemote): MissionUi {
        return MissionUi(
            title = dto.nombre,
            current = dto.progreso_actual,
            total = dto.meta,
            characterIcon = chooseMissionIcon(dto)
        )
    }

    private fun chooseMissionIcon(dto: MissionRemote): Int {
        val name = dto.nombre.lowercase()

        return when {
            "xp" in name || "experiencia" in name ->
                R.drawable.ic_mision_xp
            "leccion" in name || "lección" in name ->
                R.drawable.ic_mision_lecciones
            "modulo" in name || "módulo" in name ->
                R.drawable.ic_mision_modulo
            else ->
                R.drawable.ic_mision_xp   // fallback
        }
    }
}

// ------------------ FACTORY ------------------

class DailyMissionsViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyMissionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyMissionsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
