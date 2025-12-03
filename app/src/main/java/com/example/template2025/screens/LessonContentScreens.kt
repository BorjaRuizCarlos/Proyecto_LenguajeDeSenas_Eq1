@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import com.example.template2025.viewModel.LessonFlowViewModel
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.template2025.data.api.ApiService
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.LessonContentUiState
import com.example.template2025.viewModel.LessonContentViewModel
import com.example.template2025.viewModel.Lesson

// =====================================================================
// UTILIDADES DRIVE + MEDIA (copiadas de PalabraDiccionarioScreen)
// =====================================================================

/**
 * Convierte URLs típicas de Google Drive a un enlace "directo" que sirve
 * tanto para video como imagen.
 */
fun toDirectDriveUrlLocal(originalUrl: String): String {
    val url = originalUrl.trim()
    val regexD = """/d/([^/?]+)/?""".toRegex()
    val fromD = regexD.find(url)?.groupValues?.get(1)
    val regexQueryId = """[?&]id=([^&]+)""".toRegex()
    val fromQuery = regexQueryId.find(url)?.groupValues?.get(1)
    val fileId = fromD ?: fromQuery
    return if (fileId != null) {
        "https://drive.google.com/uc?export=download&id=$fileId"
    } else {
        url
    }
}

private val mediaHeadClientLocal by lazy { OkHttpClient() }

private enum class RemoteMediaTypeLocal2 {
    IMAGE, VIDEO, UNKNOWN, ERROR
}

// =====================================================================
// FACTORY para LessonContentViewModel
// =====================================================================
class LessonContentViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonContentViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// ... (Tus imports)

@Composable
fun LessonsContentScreen(
    navController: NavController,
    moduleId: Int?,
    lessonId: Int?,
    token: String? = null
) {
    // Validar que tenemos los datos necesarios
    if (token.isNullOrBlank() || lessonId == null) {
        return
    }

    // Crear ViewModel para contenido de lección con API usando factory
    val apiService = remember { ApiService.RetrofitClient.apiService }
    val contentViewModel: LessonContentViewModel = viewModel(
        factory = LessonContentViewModelFactory(apiService)
    )
    val contentUiState by contentViewModel.uiState.collectAsState()

    // Cargar el contenido de la lección cuando se monta la pantalla
    LaunchedEffect(token, lessonId) {
        contentViewModel.fetchLessonContent(token, lessonId)
    }

    // Mostrar estados de contenido
    when (contentUiState) {
        is LessonContentUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is LessonContentUiState.Success -> {
            val content = contentUiState as LessonContentUiState.Success
            // Convertir videos a LessonStep ordenados por ID
            val lessonSteps = content.videos.sortedBy { it.id }.map { video ->
                LessonFlowViewModel.LessonStep(
                    title = video.nombre,
                    imageUrl = video.url
                )
            }

            // Crear ViewModel del flujo con los datos del API
            val flowViewModel = remember(lessonSteps) {
                LessonFlowViewModel(
                    api=apiService,
                    token = token,
                    lessonId = lessonId,
                    lessonStepsData = lessonSteps,

                )
            }

            // Observa el estado del flujo de la lección
            val flowState by flowViewModel.currentFlowState.collectAsState()

            // Maneja la navegación y el contenido basado en el estado
            when (flowState) {
                is LessonFlowViewModel.LessonFlowState.Practice -> {
                    val step = (flowState as LessonFlowViewModel.LessonFlowState.Practice).step

                    PalabraDiccionarioScreen(
                        word = step.title,
                        mediaUrl = step.imageUrl,
                        onBack = {
                            flowViewModel.nextStep() // Avanza a la pantalla de Pregunta
                        }
                    )
                }
                is LessonFlowViewModel.LessonFlowState.Question -> {
                    val state = (flowState as LessonFlowViewModel.LessonFlowState.Question)
                    val imageRes = R.drawable.btn_abecedario_continuar

                    // Generar opciones aleatorias desde lessonSteps (solo una vez con remember)
                    val allTitles = flowViewModel.lessonSteps.map { it.title }
                    val correctTitle = state.step.title

                    // Generar opciones fijas (no cambiar de posición)
                    val opcionesFixed = remember(correctTitle) {
                        val incorrectOptions = allTitles.filter { it != correctTitle }.shuffled().take(3)
                        (listOf(correctTitle) + incorrectOptions).shuffled()
                    }
                    val correctTitleIndex = opcionesFixed.indexOf(correctTitle)

                    var selectedIndex by remember(correctTitle) { mutableStateOf(-1) }
                    var isCorrect by remember(correctTitle) { mutableStateOf(false) }
                    PreguntaLeccionScreen(
                        pregunta = "¿Qué significa la seña mostrada?",
                        respuestas = opcionesFixed,
                        mediaUrl = state.step.imageUrl,
                        imageRes = imageRes,
                        onRespuestaClick = { idx ->
                            selectedIndex = idx
                            isCorrect = opcionesFixed[idx] == correctTitle
                        },
                        onContinuar = {
                            if (isCorrect) {
                                // Acción especial si es correcta (puedes mostrar un mensaje, sumar puntos, etc)
                            }
                            flowViewModel.nextStep()
                        },
                        showContinuar = selectedIndex != -1,
                        selectedIndex = selectedIndex,
                        isCorrect = isCorrect,
                        correctAnswerIndex = correctTitleIndex
                    )
                }
                LessonFlowViewModel.LessonFlowState.FinalQuizStart -> {
                    // Pantalla de inicio del quiz final
                    FinalQuizStartScreen(
                        onStart = {
                            flowViewModel.startFinalQuiz()
                        }
                    )
                }
                is LessonFlowViewModel.LessonFlowState.FinalQuiz -> {
                    val state = (flowState as LessonFlowViewModel.LessonFlowState.FinalQuiz)
                    val questionNumber = state.questionNumber
                    val opciones = state.answers
                    val correctTitle = state.step.title
                    val correctTitleIndex = opciones.indexOf(correctTitle)

                    var selectedIndex by remember { mutableStateOf(-1) }
                    var isCorrect by remember { mutableStateOf(false) }

                    FinalQuizScreen(
                        questionNumber = questionNumber,
                        pregunta = "¿Qué significa la seña mostrada?",
                        mediaUrl = state.step.imageUrl,
                        respuestas = opciones,
                        onRespuestaClick = { idx ->
                            selectedIndex = idx
                            isCorrect = opciones[idx] == correctTitle
                        },
                        onContinuar = {
                            flowViewModel.nextStep()
                        },
                        showContinuar = selectedIndex != -1,
                        selectedIndex = selectedIndex,
                        isCorrect = isCorrect,
                        correctAnswerIndex = correctTitleIndex
                    )
                }
                LessonFlowViewModel.LessonFlowState.Finished -> {
                    // La lección ha terminado, navega de vuelta a InsideModulesScreen
                    navController.popBackStack(Route.InsideModule.route, inclusive = false)
                    // O podrías navegar a una pantalla de felicitación
                }
            }
        }
        is LessonContentUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6F0F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (contentUiState as LessonContentUiState.Error).message,
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
fun PracticaLetraScreen(
    titulo: String,
    imageUrl: String = "",
    @DrawableRes imageRes: Int? = null,
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
                    text = titulo,
                    color = BlueDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = titulo,
                color = BlueDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // ---------- TARJETA BONITA DE MEDIA (Imagen/Video) ----------
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mostrar imagen si está disponible
                    if (imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = titulo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (imageRes != null && imageRes != 0) {
                        Image(
                            painter = painterResource(id = imageRes!!),
                            contentDescription = titulo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "No hay contenido disponible",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón Continuar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(32.dp))
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
    mediaUrl: String = "",
    @DrawableRes imageRes: Int,
    onRespuestaClick: (Int) -> Unit,
    onContinuar: () -> Unit,
    showContinuar: Boolean = false,
    selectedIndex: Int = -1,
    isCorrect: Boolean = false,
    correctAnswerIndex: Int = -1,
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

            // Tarjeta de imagen / media de la seña / ejemplo
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
                if (mediaUrl.isNotBlank()) {
                    val playableUrl: String? = remember(mediaUrl) {
                        val cleaned = mediaUrl.trim()
                        if (cleaned.startsWith("http")) {
                            toDirectDriveUrlLocal(cleaned)
                        } else {
                            null
                        }
                    }
                    if (playableUrl != null) {
                        RemoteMediaContentLocal2(
                            url = playableUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Media no disponible", color = Color.White)
                        }
                    }
                } else {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Imagen de la pregunta",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
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
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 0,
                    correct = isCorrect && selectedIndex == 0,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 0
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[1],
                    onClick = { onRespuestaClick(1) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 1,
                    correct = isCorrect && selectedIndex == 1,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 1
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
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 2,
                    correct = isCorrect && selectedIndex == 2,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 2
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[3],
                    onClick = { onRespuestaClick(3) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 3,
                    correct = isCorrect && selectedIndex == 3,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 3
                )
            }

            Spacer(Modifier.weight(1f))

            // Botón Continuar solo visible si se seleccionó una opción
            if (showContinuar) {
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
                ) {Spacer(Modifier.height(4.dp))
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
}

@Composable
fun QuizOptionButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false,
    correct: Boolean = false,
    showCorrectAnswer: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        selected && correct -> Color(0xFF4CAF50) // Verde si es correcta
        selected && !correct -> Color(0xFFF44336) // Rojo si es incorrecta
        showCorrectAnswer -> Color(0xFF4CAF50) // Verde si es la respuesta correcta y se mostró después de error
        else -> Color(0xFF21409A)
    }
    Box(
        modifier = modifier
            .height(52.dp)
            .background(
                color = bgColor,
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

// -------------------- Remote media helper (copied/adapted) --------------------

private enum class RemoteMediaTypeLocal {
    IMAGE, VIDEO, UNKNOWN, ERROR
}

@Composable
fun VideoPlayerLocal(
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

    val context = LocalContext.current

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(url) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = modifier
    )
}

@Composable
fun RemoteMediaContentLocal(
    url: String,
    modifier: Modifier = Modifier
) {
    var mediaType by remember(url) { mutableStateOf(RemoteMediaTypeLocal.UNKNOWN) }
    var loading by remember(url) { mutableStateOf(true) }

    LaunchedEffect(url) {
        loading = true
        mediaType = RemoteMediaTypeLocal.UNKNOWN

        if (!url.startsWith("http")) {
            mediaType = RemoteMediaTypeLocal.ERROR
            loading = false
            return@LaunchedEffect
        }

        try {
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            val ct = withContext(Dispatchers.IO) {
                mediaHeadClientLocal.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        null
                    } else {
                        resp.header("Content-Type")
                    }
                }
            }?.lowercase().orEmpty()

            mediaType = when {
                ct.startsWith("video/") -> RemoteMediaTypeLocal.VIDEO
                ct.startsWith("image/") -> RemoteMediaTypeLocal.IMAGE
                ct.isNotBlank()         -> RemoteMediaTypeLocal.UNKNOWN
                else                    -> RemoteMediaTypeLocal.ERROR
            }
        } catch (_: Exception) {
            mediaType = RemoteMediaTypeLocal.ERROR
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

            mediaType == RemoteMediaTypeLocal.IMAGE -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            mediaType == RemoteMediaTypeLocal.VIDEO ||
                    mediaType == RemoteMediaTypeLocal.UNKNOWN -> {
                VideoPlayerLocal(
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
/*          PANTALLA: INICIO DEL QUIZ FINAL (5 preguntas)           */
/* ---------------------------------------------------------------- */

@Composable
fun FinalQuizStartScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono/Título
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color(0xFF5F74BD),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📚",
                    fontSize = 48.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            // Título
            Text(
                text = "¡Quiz Final!",
                color = BlueDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Ahora viene el quiz final de la sesión",
                color = BlueDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Detalles

            Spacer(Modifier.weight(1f))

            // Botón Comenzar
            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color(0xFF21409A),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onStart() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Comenzar Quiz",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/*          PANTALLA: QUIZ FINAL (5 preguntas seguidas)             */
/* ---------------------------------------------------------------- */

@Composable
fun FinalQuizScreen(
    questionNumber: Int,
    pregunta: String,
    mediaUrl: String = "",
    respuestas: List<String>,
    onRespuestaClick: (Int) -> Unit,
    onContinuar: () -> Unit,
    showContinuar: Boolean = false,
    selectedIndex: Int = -1,
    isCorrect: Boolean = false,
    correctAnswerIndex: Int = -1,
    modifier: Modifier = Modifier
) {
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
            Spacer(Modifier.height(16.dp))

            // Número de pregunta
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF5F74BD),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Pregunta $questionNumber / 5",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tarjeta de media (URL de la respuesta correcta)
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
                if (mediaUrl.isNotBlank()) {
                    val playableUrl: String? = remember(mediaUrl) {
                        val cleaned = mediaUrl.trim()
                        if (cleaned.startsWith("http")) {
                            toDirectDriveUrlLocal(cleaned)
                        } else {
                            null
                        }
                    }
                    if (playableUrl != null) {
                        RemoteMediaContentLocal2(
                            url = playableUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Media no disponible", color = Color.White)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF4E62A7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Media no disponible",
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Texto de la pregunta
            Text(
                text = pregunta,
                color = BlueDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Respuestas 1 y 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuizOptionButton(
                    text = opciones[0],
                    onClick = { onRespuestaClick(0) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 0,
                    correct = isCorrect && selectedIndex == 0,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 0
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[1],
                    onClick = { onRespuestaClick(1) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 1,
                    correct = isCorrect && selectedIndex == 1,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 1
                )
            }

            Spacer(Modifier.height(12.dp))

            // Respuestas 3 y 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuizOptionButton(
                    text = opciones[2],
                    onClick = { onRespuestaClick(2) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 2,
                    correct = isCorrect && selectedIndex == 2,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 2
                )
                Spacer(Modifier.width(16.dp))
                QuizOptionButton(
                    text = opciones[3],
                    onClick = { onRespuestaClick(3) },
                    modifier = Modifier.weight(1f),
                    selected = selectedIndex == 3,
                    correct = isCorrect && selectedIndex == 3,
                    showCorrectAnswer = selectedIndex != -1 && !isCorrect && correctAnswerIndex == 3
                )
            }

            Spacer(Modifier.weight(1f))

            // Botón Continuar
            if (showContinuar) {
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
}

/* ---------------------------------------------------------------- */
/* -------------------- MEDIA DISPLAY COMPONENT ------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun RemoteMediaContentLocal2(url: String, modifier: Modifier = Modifier) {
    var mediaType by remember(url) { mutableStateOf(RemoteMediaTypeLocal2.UNKNOWN) }
    var loading by remember(url) { mutableStateOf(true) }

    LaunchedEffect(url) {
        loading = true
        mediaType = RemoteMediaTypeLocal2.UNKNOWN

        if (!url.startsWith("http")) {
            mediaType = RemoteMediaTypeLocal2.ERROR
            loading = false
            return@LaunchedEffect
        }

        try {
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            val ct = withContext(Dispatchers.IO) {
                mediaHeadClientLocal.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        null
                    } else {
                        resp.header("Content-Type")
                    }
                }
            }?.lowercase().orEmpty()

            mediaType = when {
                ct.startsWith("video/") -> RemoteMediaTypeLocal2.VIDEO
                ct.startsWith("image/") -> RemoteMediaTypeLocal2.IMAGE
                ct.isNotBlank() -> RemoteMediaTypeLocal2.UNKNOWN
                else -> RemoteMediaTypeLocal2.ERROR
            }
        } catch (_: Exception) {
            mediaType = RemoteMediaTypeLocal2.ERROR
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

            mediaType == RemoteMediaTypeLocal2.IMAGE -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            mediaType == RemoteMediaTypeLocal2.VIDEO ||
                    mediaType == RemoteMediaTypeLocal2.UNKNOWN -> {
                // UNKNOWN lo tratamos como video por defecto
                VideoPlayerLocal(
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
