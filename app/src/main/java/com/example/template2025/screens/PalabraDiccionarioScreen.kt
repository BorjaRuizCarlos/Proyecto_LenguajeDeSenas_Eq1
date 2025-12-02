@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.WordDetailViewModel
import com.example.template2025.data.api.WordDetailViewModelFactory
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme

/* ---------------------------------------------------------------- */
/* -----------------   ROUTE (LÓGICA + VM)   ----------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun PalabraDiccionarioRoute(
    wordId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Instancia de la API
    val apiService = remember { ApiService.RetrofitClient.apiService }

    // ViewModel de detalle
    val vm: WordDetailViewModel = viewModel(
        factory = WordDetailViewModelFactory(apiService)
    )

    // StateFlow -> State Compose
    val uiState by vm.uiState.collectAsState()

    // Cargar la palabra cuando cambie el id
    LaunchedEffect(wordId) {
        vm.loadWord(wordId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.loading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                Text(
                    text = "Error al cargar palabra:\n${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            uiState.word != null -> {
                val word = uiState.word!!
                PalabraDiccionarioScreen(
                    word = word.titulo,
                    videoUrl = word.url,
                    onBack = onBack
                )
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/* -----------------   PANTALLA DE DETALLE   ----------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun PalabraDiccionarioScreen(
    word: String,
    videoUrl: String,
    onBack: () -> Unit,
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

            // Título principal con "subrayado" de fondo
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
                    text = word,
                    color = BlueDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = word,
                color = BlueDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // Tarjeta grande donde (por ahora) mostramos la URL del video.
            // Más adelante puedes meter un VideoView / ExoPlayer.
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(3f / 4f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5F74BD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Video URL:\n$videoUrl",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .width(170.dp)
                    .height(56.dp)
                    .background(
                        color = Color(0xFF21409A),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Volver",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ---------- PREVIEW ---------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PalabraDiccionarioPreview() {
    Template2025Theme {
        PalabraDiccionarioScreen(
            word = "Palabra",
            videoUrl = "https://ejemplo.com/video.mp4",
            onBack = {}
        )
    }
}
