package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.HomeUiState
import com.example.template2025.viewModel.HomeViewModel

// =====================================================================
// FACTORY para HomeViewModel
// =====================================================================
class HomeViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// =====================================================================
// DATA CLASSES PARA LA UI
// =====================================================================
data class DayProgress(val day: String, val completed: Boolean)
data class Mission(val name: String, val current: Int, val max: Int)
data class Module(val name: String, val current: Int, val max: Int)
val CardBackgroundColor = Color(0xDFF0FFFF)

data class AppData(
    val dailyProgress: List<DayProgress>,
    val dailyMissions: List<Mission>,
    val generalProgress: Mission,
    val streakDays: Int,
    val lessons: List<Module>
)

// Datos fake SOLO para el preview
fun getFakeApiData(): AppData = AppData(
    dailyProgress = listOf(
        DayProgress("L", true),
        DayProgress("M", true),
        DayProgress("M", true),
        DayProgress("J", true),
        DayProgress("V", false),
        DayProgress("S", false),
        DayProgress("D", false)
    ),
    dailyMissions = listOf(
        Mission("Misión 1", 43, 50),
        Mission("Misión 2", 42, 50),
        Mission("Misión 3", 41, 50)
    ),
    generalProgress = Mission("Progreso General", 30, 100),
    streakDays = 7,
    lessons = listOf(
        Module("Módulo 1", 43, 50),
        Module("Módulo 2", 41, 50),
        Module("Módulo 3", 41, 50)
    )
)

// =====================================================================
// PREVIEW (usa sólo datos fake)
// =====================================================================
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val appData = getFakeApiData()
    HomeScreenContent(
        appData = appData,
        token = "TOKEN_DE_PREVIEW_123",
        onNavigateToDailyQuests = {}
    )
}

// =====================================================================
// HOME SCREEN: recibe token desde MainScaffold
// =====================================================================
@Composable
fun HomeScreen(
    navController: NavController,
    token: String?
) {
    // Si no hay token, avisamos y NO llamamos a la API
    if (token.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB2CAF5FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró token.\nVuelve a iniciar sesión.",
                color = Color(0xFF244984),
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    val apiService = remember { ApiService.RetrofitClient.apiService }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(apiService)
    )

    // Llamamos al backend cada vez que cambie el token
    LaunchedEffect(token) {
        homeViewModel.fetchHomeData(token)
    }

    val uiState by homeViewModel.uiState.collectAsState()

    when (uiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB2CAF5FF)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HomeUiState.Error -> {
            val message = (uiState as HomeUiState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB2CAF5FF))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar home:\n$message",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        is HomeUiState.Success -> {
            val appData = (uiState as HomeUiState.Success).appData
            HomeScreenContent(
                appData = appData,
                token = token,
                onNavigateToDailyQuests = {
                    navController.navigate(Route.DailyQuests.route)
                }
            )
        }
    }
}

// =====================================================================
// COMPONENTE DE PROGRESO POR MÓDULO
// =====================================================================
@Composable
fun ModuleProgressBar(module: Module) {
    val progress = module.current / module.max.toFloat()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = module.name,
            color = Color(0xFF244984),
            modifier = Modifier.width(80.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "${module.current}/${module.max}",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// =====================================================================
// COMPONENTE DE PROGRESO POR MISIÓN
// =====================================================================
@Composable
fun MissionProgressBar(mission: Mission) {
    val progress = mission.current / mission.max.toFloat()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = mission.name,
            color = Color(0xFF244984),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "${mission.current}/${mission.max}",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// =====================================================================
// CONTENIDO COMPLETO DEL HOME
// =====================================================================
@Composable
fun HomeScreenContent(
    appData: AppData,
    token: String?,               // 👈 ya no lo mostramos en UI, solo se recibe
    onNavigateToDailyQuests: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB2CAF5FF))
    ) {

        // Squibbles de fondo
        Image(
            painter = painterResource(id = R.drawable.squibble_1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(850.dp)
                .offset(x = 0.dp, y = 30.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.squibble_2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(800.dp)
                .offset(x = 0.dp, y = 100.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.squibble_3),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)   // 👈 SCROLL VERTICAL
                .padding(16.dp)
        ) {
            // 🔹 Antes aquí estaba la tarjeta del token: ya la quitamos

            // ¡Bienvenida!
            Text(
                text = "¡Bienvenido de vuelta!",
                color = Color(0xFF244984),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Progreso semanal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                appData.dailyProgress.forEach { dayProgress ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayProgress.day,
                            color = Color(0xFF244984),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Image(
                            painter = painterResource(
                                id = if (dayProgress.completed)
                                    R.drawable.ic_character_completed
                                else
                                    R.drawable.ic_character_uncompleted
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Misiones Diarias:",
                color = Color(0xFF244984),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToDailyQuests),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackgroundColor
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        appData.dailyMissions.forEach { mission ->
                            MissionProgressBar(mission = mission)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Progreso General:",
                color = Color(0xFF244984),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackgroundColor
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = appData.generalProgress.current /
                                    appData.generalProgress.max.toFloat(),
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 8.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${appData.generalProgress.current}%",
                            color = Color(0xFF244984),
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_character_completed),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "${appData.streakDays} días de racha!",
                            color = Color(0xFF244984),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Módulos",
                color = Color(0xFF244984),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackgroundColor
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    appData.lessons.forEach { lesson ->
                        ModuleProgressBar(module = lesson)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
