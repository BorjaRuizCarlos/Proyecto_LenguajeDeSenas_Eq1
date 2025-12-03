package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.LessonAnswerRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonFlowViewModel(
    private val api: ApiService, // <-- NUEVO
    private val token: String, // <-- NUEVO
    private val lessonId: Int, // <-- NUEVO
    private val lessonStepsData: List<LessonStep> = emptyList(),

) : ViewModel() {
// LessonFlowModels.kt
val lessonSteps: List<LessonStep> = lessonStepsData
    private var finalQuizScore: Int = 0 // Ya está definida, ¡perfecto!
    private val totalQuizQuestions = 5 // Se usa para calcular el porcentaje
    // Representa un par de (Título/Letra, URL de la Imagen)
    data class LessonStep(
        val title: String,
        val imageUrl: String // Usaremos un String para simular la URL
        // Aquí puedes añadir más datos si necesitas (ej. audioUrl, datos de pregunta)
    )

    sealed class LessonFlowState {
        // Estado de la práctica (mostrando el paso actual)
        data class Practice(val step: LessonStep) : LessonFlowState()
        // Estado de la pregunta (mostrando el quiz)
        data class Question(val step: LessonStep, val answers: List<String>) : LessonFlowState()
        // Pantalla de inicio del quiz final (5 preguntas)
        object FinalQuizStart : LessonFlowState()
        // Estado del quiz final (5 preguntas seguidas)
        data class FinalQuiz(val step: LessonStep, val answers: List<String>, val questionNumber: Int = 1) : LessonFlowState()
        // Lección terminada
        object Finished : LessonFlowState()
    }

    // El índice actual en la lista de pasos
    private var currentStepIndex: Int = 0

    // Bandera para alternar entre Practica y Pregunta (true = Pregunta, false = Practica)
    private var isQuestionPhase: Boolean = false

    // Bandera para controlar si estamos en el quiz final
    private var isFinalQuiz: Boolean = false
    private var finalQuizCount: Int = 0
    private var finalQuizSteps: List<LessonStep> = emptyList()

    private val _currentFlowState = MutableStateFlow<LessonFlowState>(
        if (lessonStepsData.isNotEmpty()) {
            LessonFlowState.Practice(lessonStepsData[0])
        } else {
            LessonFlowState.Finished
        }
    )
    val currentFlowState: StateFlow<LessonFlowState> = _currentFlowState.asStateFlow()

    // Inicializa el flujo con el primer paso
    init {
        // Si hay datos, comienza con el primer paso, si no, muestra Finished
        if (lessonStepsData.isNotEmpty()) {
            _currentFlowState.value = LessonFlowState.Practice(lessonStepsData.first())
        }
    }

    /**
     * Avanza al siguiente estado (o al siguiente elemento si la pregunta ya fue respondida).
     * @param answeredCorrectly Indica si la pregunta se respondió correctamente (opcional).
     */
    fun nextStep(answeredCorrectly: Boolean = true) {
        if (isFinalQuiz) {
            // Estamos en el quiz final de 5 preguntas

            // **NUEVO: Contar la respuesta correcta**
            if (answeredCorrectly) {
                finalQuizScore++
            }

            finalQuizCount++

            if (finalQuizCount < totalQuizQuestions) { // Usamos la variable para claridad
                // Aún quedan preguntas del quiz final
                val nextStep = finalQuizSteps[finalQuizCount]
                val answers = generateQuizAnswers(nextStep.title)
                _currentFlowState.value = LessonFlowState.FinalQuiz(nextStep, answers, finalQuizCount + 1)
            } else {
                // Quiz final terminado
                // **NUEVO: Llamar a la API para enviar la calificación**
                sendFinalScore()

                _currentFlowState.value = LessonFlowState.Finished
            }
            return
        }
        if (!isQuestionPhase) {
            // FASE 1: Estamos en la pantalla de Practica (Letra/Palabra). Pasamos a la Pregunta.

            // Simulación de las opciones de respuesta para el quiz
            val currentStep = lessonStepsData[currentStepIndex]
            val answers = createFakeQuizAnswers(currentStep.title)

            _currentFlowState.value = LessonFlowState.Question(currentStep, answers)
            isQuestionPhase = true // La próxima vez que se llame, pasaremos a la siguiente Practica

        } else {
            // FASE 2: Estamos en la pantalla de Pregunta (Quiz). Pasamos a la siguiente Practica.

            currentStepIndex++ // Avanzamos al siguiente elemento de la lista
            isQuestionPhase = false // La próxima vez que se llame, pasaremos a la Pregunta

            if (currentStepIndex < lessonStepsData.size) {
                // Si aún quedan pasos, vamos a la Practica del siguiente elemento
                _currentFlowState.value = LessonFlowState.Practice(lessonStepsData[currentStepIndex])
            } else {
                // Todos los pasos práctica-pregunta terminados, iniciamos el quiz final
                initializeFinalQuiz()
            }
        }
    }

    /**
     * Inicia el quiz final: selecciona 5 steps al azar y muestra la pantalla de inicio.
     */
    private fun initializeFinalQuiz() {
        isFinalQuiz = true
        finalQuizCount = 0
        // Selecciona 5 steps al azar del total
        finalQuizSteps = lessonStepsData.shuffled().take(5)
        _currentFlowState.value = LessonFlowState.FinalQuizStart
    }

    /**
     * Comienza el primer quiz final.
     */
    fun startFinalQuiz() {
        if (finalQuizSteps.isNotEmpty()) {
            val firstStep = finalQuizSteps[0]
            val answers = generateQuizAnswers(firstStep.title)
            _currentFlowState.value = LessonFlowState.FinalQuiz(firstStep, answers, 1)
        }
    }

    /**
     * Genera 4 opciones de respuesta: 1 correcta + 3 distractores al azar.
     */
    private fun generateQuizAnswers(correctTitle: String): List<String> {
        val incorrectOptions = lessonStepsData
            .map { it.title }
            .filter { it != correctTitle }
            .shuffled()
            .take(3)
        return (listOf(correctTitle) + incorrectOptions).shuffled()
    }

    // SIMULACIÓN: Crea respuestas de quiz basadas en el título actual
    private fun createFakeQuizAnswers(correctTitle: String): List<String> {
        // En una app real, esto vendría de la API con distractores
        return listOf(correctTitle, "Perro", "Gato", "Avión").shuffled().take(4)
    }
    private fun sendFinalScore() {
        // Asegúrate de que lessonId y token no sean nulos si el ViewModel los recibe
        if (lessonId == null || token.isBlank()) {
            android.util.Log.e("LessonFlowViewModel", "ERROR: No se puede enviar calificación. Lesson ID o Token nulo/vacío.")
            return
        }

        viewModelScope.launch {
            val totalQuizQuestions = 5 // Se usa para el cálculo del porcentaje

            // 1. Calcular la calificación de 0 a 100
            val scorePercentage = ((finalQuizScore.toFloat() / totalQuizQuestions.toFloat()) * 100).toInt()

            // 2. Formatear la calificación como String y preparar el Body
            val calificacionString = scorePercentage.toString()
            val requestBody = LessonAnswerRequest(calificacion = calificacionString)
            val endpoint = "/lessons/$lessonId/answer"

            // =========================================================================
            // === LOG DETALLADO DEL REQUEST ANTES DE ENVIAR (RESUELVE TU PETICIÓN) ===
            // =========================================================================
            android.util.Log.d("LessonFlowViewModel", "========================================")
            android.util.Log.d("LessonFlowViewModel", "🚀 Enviando Calificación Final a la API")
            android.util.Log.d("LessonFlowViewModel", "  Endpoint: POST $endpoint")
            android.util.Log.d("LessonFlowViewModel", "  Lesson ID: $lessonId")
            android.util.Log.d("LessonFlowViewModel", "  ✅ Header: Authorization: Bearer TOKEN (enviando)")
            android.util.Log.d("LessonFlowViewModel", "  Body JSON: { \"calificacion\": \"$calificacionString\" }")
            android.util.Log.d("LessonFlowViewModel", "  Puntuación Final: $finalQuizScore / $totalQuizQuestions ($scorePercentage%)")
            android.util.Log.d("LessonFlowViewModel", "========================================")

            try {
                val response = api.sendLessonAnswer(
                    authorization = "Bearer $token", // <-- **AQUÍ SE ENVÍA EL BEARER TOKEN**
                    lessonId = lessonId,
                    body = requestBody
                )

                if (response.isSuccessful) {
                    // Log de éxito
                    android.util.Log.d("LessonFlowViewModel", " SUCCESS: Calificación enviada correctamente.")
                } else {
                    // Log de fallo con el código de error
                    val errorBody = response.errorBody()?.string().orEmpty()
                    android.util.Log.e("LessonFlowViewModel", " FAILURE: Fallo al enviar calificación (${response.code()}). Error: $errorBody")
                }
            } catch (e: Exception) {
                // Log de excepción (problemas de red, etc.)
                android.util.Log.e("LessonFlowViewModel", "⚠ EXCEPTION: Error de red o al parsear la respuesta: ${e.message}")
            }
        }
    }
}