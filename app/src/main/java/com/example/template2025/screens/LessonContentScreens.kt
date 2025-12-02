@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import LessonFlowViewModel
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.R
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme

/* ---------------------------------------------------------------- */
/*  PANTALLA 1: PRÁCTICA DE LETRA / PALABRA (solo botón Continuar)  */
/* ---------------------------------------------------------------- */
// LessonsContentScreen.kt (en el mismo paquete de tus screens)

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.navigation.Route

// ... (Tus imports)

@Composable
fun LessonsContentScreen(
    navController: NavController,
    moduleId: Int?,
    lessonId: Int?,
    viewModel: LessonFlowViewModel = viewModel() // Usa el ViewModel
) {
    // Observa el estado del flujo de la lección
    val flowState by viewModel.currentFlowState.collectAsState()

    // Maneja la navegación y el contenido basado en el estado
    when (flowState) {
        is LessonFlowViewModel.LessonFlowState.Practice -> {
            val step = (flowState as LessonFlowViewModel.LessonFlowState.Practice).step
            // Aquí simulas la conversión de URL a un recurso Drawable, que necesitarás implementar
            // Como no tenemos el mecanismo real para URL -> Drawable en este ejemplo,
            // usamos un placeholder.
            val imageRes = R.drawable.btn_abecedario_continuar

            PracticaLetraScreen(
                titulo = step.title,
                imageRes = imageRes,
                onContinuar = {
                    viewModel.nextStep() // Avanza a la pantalla de Pregunta
                }
            )
        }
        is LessonFlowViewModel.LessonFlowState.Question -> {
            val state = (flowState as LessonFlowViewModel.LessonFlowState.Question)
            val imageRes = R.drawable.btn_abecedario_continuar

            PreguntaLeccionScreen(
                pregunta = "¿Qué significa la seña de: ${state.step.title}?",
                respuestas = state.answers,
                imageRes = imageRes,
                onRespuestaClick = { selectedIndex ->
                    // Lógica para marcar la respuesta (no implementada aquí)
                },
                onContinuar = {
                    viewModel.nextStep() // Avanza a la Practica del siguiente elemento
                }
            )
        }
        LessonFlowViewModel.LessonFlowState.Finished -> {
            // La lección ha terminado, navega de vuelta a InsideModulesScreen
            navController.popBackStack(Route.InsideModule.route, inclusive = false)
            // O podrías navegar a una pantalla de felicitación
        }
    }
}
@Composable
fun PracticaLetraScreen(
    titulo: String,
    @DrawableRes imageRes: Int,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            // Título grande con "subrayado" de fondo
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .width(190.dp)
                        .height(18.dp)
                        .background(
                            color = Color(0xFFD6E6FF),
                            shape = RoundedCornerShape(50)
                        )
                        .align(Alignment.Center)
                )
                Text(
                    text = titulo,
                    color = BlueDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Subtítulo (puede ser la misma palabra o una descripción)
            Text(
                text = titulo,
                color = BlueDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // Tarjeta de imagen
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4f / 5f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5F74BD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Imagen de $titulo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.weight(1f))

            // Botón Continuar
            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .width(180.dp)
                    .height(56.dp)
                    .background(
                        color = Color(0xFF21409A),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onContinuar() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/*   PANTALLA 2: PREGUNTA TIPO QUIZ (4 opciones + botón Continuar)  */
/* ---------------------------------------------------------------- */

@Composable
fun PreguntaLeccionScreen(
    pregunta: String,
    respuestas: List<String>,
    @DrawableRes imageRes: Int,
    onRespuestaClick: (Int) -> Unit,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Nos aseguramos de tener 4 respuestas; si no, rellenamos
    val opciones = (respuestas + listOf("", "", "", "")).take(4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            // Título "Pregunta" con subrayado
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .width(210.dp)
                        .height(18.dp)
                        .background(
                            color = Color(0xFFD6E6FF),
                            shape = RoundedCornerShape(50)
                        )
                        .align(Alignment.Center)
                )
                Text(
                    text = "Pregunta",
                    color = BlueDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tarjeta de imagen de la seña / ejemplo
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4f / 5f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5F74BD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Imagen de la pregunta",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            // Texto de la pregunta
            Text(
                text = pregunta,
                color = BlueDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Respuestas 1 y 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuizOptionButton(
                    text = opciones[0],
                    onClick = { onRespuestaClick(0) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[1],
                    onClick = { onRespuestaClick(1) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Respuestas 3 y 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuizOptionButton(
                    text = opciones[2],
                    onClick = { onRespuestaClick(2) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[3],
                    onClick = { onRespuestaClick(3) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.weight(1f))

            // Botón Continuar
            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .width(180.dp)
                    .height(56.dp)
                    .background(
                        color = Color(0xFF21409A),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onContinuar() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuizOptionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(
                color = Color(0xFF21409A),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* ---------------------------------------------------------------- */
/* --------------------------- PREVIEWS ---------------------------- */
/* ---------------------------------------------------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PracticaLetraScreenPreview() {
    Template2025Theme {
        Scaffold { inner ->
            PracticaLetraScreen(
                titulo = "Palabra",
                imageRes = R.drawable.btn_abecedario_continuar, // placeholder
                onContinuar = {},
                modifier = Modifier.padding(inner)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreguntaLeccionScreenPreview() {
    Template2025Theme {
        Scaffold { inner ->
            PreguntaLeccionScreen(
                pregunta = "¿Cuál de estas opciones es la correcta?",
                respuestas = listOf("Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"),
                imageRes = R.drawable.btn_abecedario_continuar, // placeholder
                onRespuestaClick = {},
                onContinuar = {},
                modifier = Modifier.padding(inner)
            )
        }
    }
}
