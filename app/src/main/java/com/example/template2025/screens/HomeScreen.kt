    package com.example.template2025.screens // Asumiendo este es tu paquete de pantallas

    import HomeViewModel
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import com.example.template2025.R
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.offset
    // ... otras importaciones
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.example.template2025.navigation.Route
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    // HomeViewModelFactory.kt (en presentation.home o similar)
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.template2025.data.api.ApiService

    class HomeViewModelFactory(
        private val apiService: ApiService // <<-- La dependencia que se inyecta
    ) : ViewModelProvider.Factory {

        // Sobrescribe el método create para instanciar tu ViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                // Creamos la instancia de HomeViewModel y le pasamos el ApiService
                return HomeViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    // ... otras importaciones



    // =================================================================
    // ESTRUCTURAS DE DATOS (Las mantienes aquí o en un archivo de modelos)
    // =================================================================
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

    // Simulación de los datos que recibirías de la API
    fun getFakeApiData(): AppData {
        return AppData(
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
    }

    // =================================================================
    // COMPOSABLE DE LA PANTALLA
    // =================================================================

    /**
     * ESTE ES EL COMPOSABLE QUE SE LLAMA EN TU NAVHOST.
     * Contiene el contenido de la pantalla Home sin el Scaffold.
     */
    @Preview(showBackground = true)
    @Composable
    fun HomeScreenPreview() {
        val appData = getFakeApiData()
        // Una versión de preview simple sin navegación real
        HomeScreenContent(appData = appData, onNavigateToDailyQuests = {})
    }

    @Composable
    fun HomeScreen(
        navController: NavController,
        // El ViewModel se inyecta o crea automáticamente por Compose
        viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(apiService = ApiService.RetrofitClient.apiService))
    ) {
        // Observa el estado del ViewModel
        val uiState by viewModel.uiState.collectAsState()

        // Manejo de los estados (Loading, Error, Success)
        when (uiState) {
            is HomeUiState.Loading -> {
                // Mostrar un indicador de carga centrado
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                // Mostrar un mensaje de error
                val message = (uiState as HomeUiState.Error).message
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
                }
            }
            is HomeUiState.Success -> {
                val appData = (uiState as HomeUiState.Success).appData
                // Cargar el contenido de la pantalla con los datos del API
                HomeScreenContent(
                    appData = appData,
                    onNavigateToDailyQuests = {
                        navController.navigate(Route.DailyQuests.route)
                    }
                )
            }
        }
    }

    @Composable
    fun ModuleProgressBar(module: Module) {
        val progress = module.current / module.max.toFloat()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = module.name,color=  Color(0xFF244984), modifier = Modifier.width(80.dp),fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f) // Ocupa el ancho restante
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

    @Composable
    fun MissionProgressBar(mission: Mission) {
        val progress = mission.current / mission.max.toFloat()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = mission.name,color=  Color(0xFF244984), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp),fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(80.dp) // Ancho fijo para las barras pequeñas
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

    // ... (Asegúrate de incluir las definiciones de MissionProgressBar y ModuleProgressBar)
    // @Composable fun ModuleProgressBar(...) // Debe estar definida aquí o en un archivo de utilidades
    // @Composable fun MissionProgressBar(...) // Debe estar definida aquí o en un archivo de utilidades
    // ... (Tus imports, data classes, y funciones MissionProgressBar, ModuleProgressBar permanecen iguales)

    @Composable
    fun HomeScreenContent(
        appData: AppData,
        onNavigateToDailyQuests: () -> Unit // <--- Nuevo parámetro
    ) {
        // 1. Usar un Box para apilar el fondo (squibbles) y el contenido (Column)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB2CAF5FF)) // Fondo suave
        ) {

            // ===================================
            // SQUIBBLES DE FONDO (Capas inferiores)
            // ===================================
            // Squibble 1 (La forma en la esquina inferior derecha)
            Image(
                painter = painterResource(id = R.drawable.squibble_1), // ¡Asegúrate de que este recurso exista!
                contentDescription = null,
                // Posicionarlo en la esquina inferior derecha o similar
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(850.dp) // Tamaño ajustado
                    .offset(x = 0.dp, y = 30.dp) // Empujarlo ligeramente fuera para el efecto de borde
            )

            // Squibble 2 (La forma en la esquina superior izquierda)
            Image(
                painter = painterResource(id = R.drawable.squibble_2), // ¡Asegúrate de que este recurso exista!
                contentDescription = null,
                // Posicionarlo en la esquina superior izquierda o similar
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(800.dp) // Tamaño ajustado
                    .offset(x = (0).dp, y = (100).dp) // Empujarlo ligeramente fuera
            )

            // Squibble 3 (La forma larga y horizontal, debajo del progreso semanal)
            Image(
                painter = painterResource(id = R.drawable.squibble_3), // ¡Asegúrate de que este recurso exista!
                contentDescription = null,
                // Centrarlo horizontalmente, y colocarlo en la parte superior.
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(80.dp) // Altura ajustada
                    .offset(y = 0.dp) // Moverlo hacia abajo para que quede debajo del encabezado
            )

            // ===================================
            // CONTENIDO PRINCIPAL (Capa superior)
            // ===================================
            // El contenido de tu pantalla debe ir en una columna encima de los squibbles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Padding para todo el contenido
            ) {
                // Sección 1: Progreso Semanal (Tu código existente)
                Text(
                    text = "¡Bienvenido de vuelta!",
                    color=  Color(0xFF244984),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ... (Resto de tu código para el Progreso Semanal)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    appData.dailyProgress.forEach { dayProgress ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = dayProgress.day, color=  Color(0xFF244984), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Image(
                                painter = painterResource(
                                    id = if (dayProgress.completed) R.drawable.ic_character_completed else R.drawable.ic_character_uncompleted
                                ), // Reemplaza con tus propios drawables
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Misiones Diarias:",
                    color=  Color(0xFF244984),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                // Sección 2: Misiones Diarias (Tu código existente)
                Card(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToDailyQuests),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackgroundColor),
                    border= CardDefaults.outlinedCardBorder()
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
                    color=  Color(0xFF244984),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                // Sección 3: Progreso General (Tu código existente)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackgroundColor),
                    border= CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Barra circular de progreso
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = appData.generalProgress.current / appData.generalProgress.max.toFloat(),
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 8.dp,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "${appData.generalProgress.current}%",
                                color=  Color(0xFF244984),
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        // Imagen y texto de racha
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_character_completed), // Reemplaza con tu imagen de mascota
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                text = "${appData.streakDays} días de racha!",
                                color=  Color(0xFF244984),
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
                    color=  Color(0xFF244984),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                // Sección 4: Lecciones (Tu código existente)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackgroundColor),
                    border= CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        appData.lessons.forEach { lesson ->
                            ModuleProgressBar(module = lesson)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }