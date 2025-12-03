package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.data.api.ApiService
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.InsideModulesUiState
import com.example.template2025.viewModel.InsideModulesViewModel
import com.example.template2025.viewModel.Lesson

// =====================================================================
// FACTORY para InsideModulesViewModel
// =====================================================================
class InsideModulesViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsideModulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsideModulesViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// =====================================================================
// INSIDE MODULES SCREEN: recibe token desde MainScaffold
// =====================================================================
@Composable
fun InsideModulesScreen(
    navController: NavController,
    moduleId: Int?,
    token: String?
) {
    android.util.Log.d("InsideModulesScreen", "========== SCREEN INICIALIZADA ==========")
    android.util.Log.d("InsideModulesScreen", "moduleId=$moduleId")
    android.util.Log.d("InsideModulesScreen", "token=${token?.take(20)}...")

    // Si no hay token, avisamos y NO llamamos a la API
    if (token.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F0F8)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró token.\nVuelve a iniciar sesión.",
                color = Color(0xFF21409A),
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    // Si no hay moduleId, avisamos
    if (moduleId == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F0F8)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró el módulo.",
                color = Color(0xFF21409A),
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    val apiService = remember { ApiService.RetrofitClient.apiService }

    val insideModulesViewModel: InsideModulesViewModel = viewModel(
        factory = InsideModulesViewModelFactory(apiService)
    )

    // Llamamos al backend cada vez que cambie el token o moduleId
    LaunchedEffect(token, moduleId) {
        android.util.Log.d("InsideModulesScreen", "LaunchedEffect llamado: token='${token?.take(20)}...', moduleId=$moduleId")
        insideModulesViewModel.fetchLeccionesData(token, moduleId)
    }

    val uiState by insideModulesViewModel.uiState.collectAsState()

    when (uiState) {
        is InsideModulesUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6F0F8)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is InsideModulesUiState.Success -> {
            val successState = uiState as InsideModulesUiState.Success
            InsideModulesScreenContent(
                navController = navController,
                moduloNombre = successState.moduloNombre,
                lecciones = successState.lecciones,
                token = token,
                moduloId = moduleId
            )
        }

        is InsideModulesUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6F0F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as InsideModulesUiState.Error).message,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun InsideModulesScreenContent(
    navController: NavController,
    moduloNombre: String,
    lecciones: List<Lesson>,
    token: String?,
    moduloId: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // ---------- TÍTULO ----------
        Text(
            text = moduloNombre,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF21409A),
            textAlign = TextAlign.Center
        )

        // Onda debajo del título
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(16.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD5E5FF))
        )

        Spacer(Modifier.height(24.dp))

        // ---------- LISTA DE LECCIONES ----------
        LazyColumn {
            itemsIndexed(lecciones) { index, lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = {
                        // Log del ID de la lección
                        android.util.Log.d("InsideModulesScreen", "========== CLICK EN LECCIÓN ==========")
                        android.util.Log.d("InsideModulesScreen", "lesson.id=${lesson.id}")
                        android.util.Log.d("InsideModulesScreen", "lesson.title=${lesson.title}")
                        // Navegar a la pantalla de contenido de la lección
                        navController.navigate(
                            Route.LessonsContent.createRoute(
                                moduleId = moduloId ?: 0,
                                lessonId = lesson.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCEE2F7)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF21409A))
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Título de la lección
            Text(
                text = lesson.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF21409A)
            )

            Spacer(Modifier.height(12.dp))

            // Barra de progreso
            LessonProgressBar(
                current = lesson.progress,
                total = lesson.total
            )
        }
    }
}

@Composable
fun LessonProgressBar(
    current: Int,
    total: Int
) {
    val fraction =
        if (total > 0) (current.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.CenterStart
    ) {
        // Parte azul de progreso
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF21409A))
        )

        // Texto centrado
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$current/$total",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InsideModulesScreenPreview() {
    InsideModulesScreen(navController = rememberNavController(), moduleId = 1, token = "test_token")
}
