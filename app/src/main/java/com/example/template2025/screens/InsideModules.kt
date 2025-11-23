package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(16.dp)
    ) {
        if (module != null) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // usamos itemsIndexed para obtener el índice de la lección
                itemsIndexed(lessons) { index, lesson ->
                    LessonCard(
                        lesson = lesson,
                        onClick = {
                            val currentModuleId = moduleId ?: 0
                            // usamos el índice como lessonId en la navegación
                            navController.navigate(
                                Route.LessonPractice.createRoute(
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
            .clickable { onClick() },   // ahora la tarjeta es clickeable
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC0D6E8))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lesson.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = lesson.progress.toFloat() / lesson.total,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF0047AB),
                    trackColor = Color.White,
                )
                Text(
                    text = "${lesson.progress}/${lesson.total}",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InsideModulesScreenPreview() {
    InsideModulesScreen(navController = rememberNavController(), moduleId = 1)
}
