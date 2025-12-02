package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.Lesson
import com.example.template2025.viewModel.ModuleViewModel

@Composable
fun InsideModulesScreen(
    navController: NavController,
    moduleId: Int?,
    moduleViewModel: ModuleViewModel = viewModel()
) {
    val module = moduleViewModel.getModule(moduleId)
    val lessons = moduleViewModel.getLessonsForModule(moduleId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (module != null) {

            Spacer(Modifier.height(8.dp))

            // ---------- TÍTULO “Módulo 1” ----------
            Text(
                text = module.title,
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
            LazyColumn(
                // ...
            ) {
                itemsIndexed(lessons) { index, lesson ->
                    LessonCard(
                        lesson = lesson,
                        onClick = {
                            val currentModuleId = moduleId ?: 0
                            // *** CAMBIAR ESTO ***
                            navController.navigate(
                                Route.LessonsContent.createRoute( // <<< Usar la nueva ruta
                                    moduleId = currentModuleId,
                                    lessonId = index
                                )
                            )
                        }
                    )
                }
            }
        } else {
            Text(text = "Módulo no encontrado")
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
            containerColor = Color(0xFFCEE2F7) // fondo azul muy claro
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

            // Barra de progreso tipo “píldora” con texto dentro
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
            .background(Color(0xFFFFFFFF)), // track blanco
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

        // Texto centrado encima (43/50)
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
    InsideModulesScreen(navController = rememberNavController(), moduleId = 1)
}
