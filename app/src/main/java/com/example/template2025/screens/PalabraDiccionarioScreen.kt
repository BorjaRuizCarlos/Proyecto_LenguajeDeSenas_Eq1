@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.WordDetailViewModel
import com.example.template2025.data.api.WordDetailViewModelFactory
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/* ---------------------------------------------------------------- */
/* -----------------   ROUTE (LÓGICA + VM)   ----------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun PalabraDiccionarioRoute(
    wordId: Int,
    token: String?,          // <- recibe token desde MainScaffold
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val apiService = remember { ApiService.RetrofitClient.apiService }

    val vm: WordDetailViewModel = viewModel(
        factory = WordDetailViewModelFactory(apiService)
    )

    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(wordId, token) {
        if (!token.isNullOrBlank()) {
            vm.loadWord(wordId, token)   // <- tu VM debe aceptar token y usar Authorization
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight),
        contentAlignment = Alignment.Center
    ) {
        when {
            token.isNullOrBlank() -> {
                Text(
                    text = "No se encontró token.\nVuelve a iniciar sesión.",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }

            uiState.loading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                Text(
                    text = "Error al cargar palabra:\n${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }

            uiState.word != null -> {
                val word = uiState.word!!
                PalabraDiccionarioScreen(
                    word = word.titulo,
                    mediaUrl = word.url,
                    onBack = onBack
                )
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/* -----------------   UTILIDADES DRIVE + MEDIA  ------------------- */
/* ---------------------------------------------------------------- */

/**
 * Convierte URLs típicas de Google Drive a un enlace "directo" que sirve
 * tanto para video como imagen:
 *
 *  - https://drive.google.com/file/d/FILE_ID/view?usp=drive_link
 *  - https://drive.google.com/open?id=FILE_ID
 *
 * Resultado: https://drive.google.com/uc?export=download&id=FILE_ID
 */
fun toDirectDriveUrl(originalUrl: String): String {
    val url = originalUrl.trim()

    // 1) Formato /d/FILE_ID/
    val regexD = """/d/([^/?]+)/?""".toRegex()
    val fromD = regexD.find(url)?.groupValues?.get(1)

    // 2) Formato ?id=FILE_ID
    val regexQueryId = """[?&]id=([^&]+)""".toRegex()
    val fromQuery = regexQueryId.find(url)?.groupValues?.get(1)

    val fileId = fromD ?: fromQuery

    return if (fileId != null) {
        "https://drive.google.com/uc?export=download&id=$fileId"
    } else {
        url
    }
}


// Cliente HTTP para hacer HEAD y detectar tipo de contenido
private val mediaHeadClient by lazy { OkHttpClient() }

private enum class RemoteMediaType {
    IMAGE, VIDEO, UNKNOWN, ERROR
}

/* ---------------------------------------------------------------- */
/* -----------------   COMPONENTES DE MEDIA  ----------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier
) {
    if (!url.startsWith("http")) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "URL de video inválida",
                color = Color.White
            )
        }
        return
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

/**
 * Detecta via HEAD si la URL es video o imagen y pinta lo correcto.
 */
@Composable
private fun RemoteMediaContent(
    url: String,
    modifier: Modifier = Modifier
) {
    var mediaType by remember(url) { mutableStateOf(RemoteMediaType.UNKNOWN) }
    var loading by remember(url) { mutableStateOf(true) }

    LaunchedEffect(url) {
        loading = true
        mediaType = RemoteMediaType.UNKNOWN

        if (!url.startsWith("http")) {
            mediaType = RemoteMediaType.ERROR
            loading = false
            return@LaunchedEffect
        }

        try {
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            val ct = withContext(Dispatchers.IO) {
                mediaHeadClient.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        null
                    } else {
                        resp.header("Content-Type")
                    }
                }
            }?.lowercase().orEmpty()

            mediaType = when {
                ct.startsWith("video/") -> RemoteMediaType.VIDEO
                ct.startsWith("image/") -> RemoteMediaType.IMAGE
                ct.isNotBlank()         -> RemoteMediaType.UNKNOWN
                else                    -> RemoteMediaType.ERROR
            }
        } catch (_: Exception) {
            mediaType = RemoteMediaType.ERROR
        }

        loading = false
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> {
                CircularProgressIndicator(color = Color.White)
            }

            mediaType == RemoteMediaType.IMAGE -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            mediaType == RemoteMediaType.VIDEO ||
                    mediaType == RemoteMediaType.UNKNOWN -> {
                // UNKNOWN lo tratamos como video por defecto
                VideoPlayer(
                    url = url,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Text(
                    text = "Media no disponible",
                    color = Color.White
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
    mediaUrl: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playableUrl: String? = remember(mediaUrl) {
        val cleaned = mediaUrl.trim()
        if (cleaned.startsWith("http")) {
            toDirectDriveUrl(cleaned)
        } else {
            null
        }
    }

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
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = word,
                color = BlueDark,
                fontSize = 22.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // ---------- TARJETA BONITA DE MEDIA ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(3f / 4f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5F74BD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Chip "Contenido multimedia"
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF324E9F),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Contenido multimedia",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color(0xFF4E62A7),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playableUrl == null) {
                            Text(
                                text = "Media no disponible",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )
                        } else {
                            RemoteMediaContent(
                                url = playableUrl,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
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
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
            mediaUrl = "https://drive.google.com/file/d/FILE_ID/view?usp=drive_link",
            onBack = {}
        )
    }
}
