// HomeViewModel.kt (en un paquete como com.example.template2025.presentation.home)
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

// Estado de la UI para manejar carga, datos o error
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val appData: AppData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Mapeador de datos del API a la UI
    private fun mapToAppData(response: HomeResponse): AppData {
        return AppData(
            dailyProgress = response.dailyProgress.map { DayProgress(it.day, it.completed) },
            dailyMissions = response.dailyMissions.map { Mission(it.name, it.current, it.max) },
            generalProgress = Mission(response.generalProgress.name, response.generalProgress.current, response.generalProgress.max),
            streakDays = response.streakDays,
            lessons = response.lessons.map { Module(it.name, it.current, it.max) }
        )
    }

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.getHomeData()
                val appData = mapToAppData(response)
                _uiState.value = HomeUiState.Success(appData)
            } catch (e: Exception) {
                // Manejo de errores de red o deserialización
                _uiState.value = HomeUiState.Error("Fallo al cargar datos: ${e.localizedMessage}")
                // En caso de fallo, se podría intentar cargar datos guardados en caché si existen
            }
        }
    }
}