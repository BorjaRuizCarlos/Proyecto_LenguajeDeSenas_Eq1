    package com.example.template2025.screens // Asumiendo este es tu paquete de pantallas

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
    // ... otras importaciones
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp


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
    fun HomeScreen() {
        val appData = getFakeApiData() // Aquí obtienes los datos harcodeados o de la API

        // Simplemente llamamos a la función de contenido
        HomeScreenContent(appData = appData)
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

    @Composable
    fun HomeScreenContent(appData: AppData) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB2CAF5FF)) // Un fondo suave como en tu imagen
                .padding(16.dp)
        ) {
            // Sección 1: Progreso Semanal
            Text(
                text = "¡Bienvenido de vuelta!",
                color=  Color(0xFF244984),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
            // Sección 2: Misiones Diarias
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackgroundColor),
                border= CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                    }
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
            // Sección 3: Progreso General
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lecciones",
                color=  Color(0xFF244984),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            // Sección 4: Lecciones
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
    // @Composable fun MissionProgressBar(...) // Debe estar definida aquí o en un archivo de utilidades
    // @Composable fun ModuleProgressBar(...) // Debe estar definida aquí o en un archivo de utilidades
    // ... (Asegúrate de incluir las definiciones de MissionProgressBar y ModuleProgressBar)