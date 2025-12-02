// LessonFlowViewModel.kt

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LessonFlowViewModel : ViewModel() {
// LessonFlowModels.kt

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
        // Lección terminada
        object Finished : LessonFlowState()
    }
    // Simulación de la lista de datos obtenida de la API
    private val lessonStepsData: List<LessonStep> = listOf(
        LessonStep("Abeja", "url_abeja.jpg"),
        LessonStep("Bicicleta", "url_bicicleta.jpg"),
        LessonStep("Casa", "url_casa.jpg"),
        // ... Agrega más pasos
    )

    // El índice actual en la lista de pasos
    private var currentStepIndex: Int = 0

    // Bandera para alternar entre Practica y Pregunta (true = Pregunta, false = Practica)
    private var isQuestionPhase: Boolean = false

    private val _currentFlowState = MutableStateFlow<LessonFlowState>(LessonFlowState.Practice(lessonStepsData[0]))
    val currentFlowState: StateFlow<LessonFlowState> = _currentFlowState.asStateFlow()

    // Inicializa el flujo con el primer paso
    init {
        // Asume que la llamada al API ya se hizo y lessonStepsData está lleno
        _currentFlowState.value = LessonFlowState.Practice(lessonStepsData.first())
    }

    /**
     * Avanza al siguiente estado (o al siguiente elemento si la pregunta ya fue respondida).
     * @param answeredCorrectly Indica si la pregunta se respondió correctamente (opcional).
     */
    fun nextStep(answeredCorrectly: Boolean = true) {
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
                // Si terminamos, finalizamos la lección
                _currentFlowState.value = LessonFlowState.Finished
            }
        }
    }

    // SIMULACIÓN: Crea respuestas de quiz basadas en el título actual
    private fun createFakeQuizAnswers(correctTitle: String): List<String> {
        // En una app real, esto vendría de la API con distractores
        return listOf(correctTitle, "Perro", "Gato", "Avión").shuffled().take(4)
    }
}