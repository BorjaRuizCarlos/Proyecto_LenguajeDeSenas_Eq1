package com.example.template2025.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// En una app más grande, estos modelos estarían en su propio paquete (p. ej., 'data/models')
data class Module(
    val id: Int,
    val title: String,
    val subtitle: String,
    val progress: Float, // Progreso de 0.0 a 1.0
    val unlocked: Boolean
)

data class Lesson(
    val title: String,
    val progress: Int,
    val total: Int
)

class ModuleViewModel : ViewModel() {

    // Datos de ejemplo que simulan venir de una API o base de datos.
    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    // Un mapa que asocia el ID de un módulo con su lista de lecciones.
    private val allLessons = mapOf(
        1 to listOf(
            Lesson("Abecedario", 43, 50),
            Lesson("Animales (Módulo 1)", 30, 50),
            Lesson("Colores (Módulo 1)", 25, 50)
        ),
        2 to listOf(
            Lesson("Lección A (Módulo 2)", 10, 20),
            Lesson("Lección B (Módulo 2)", 5, 20)
        ),
        3 to listOf(
            Lesson("Lección X (Módulo 3)", 0, 100),
            Lesson("Lección Y (Módulo 3)", 50, 100),
        ),
        4 to listOf(
            Lesson("Pregunta Final 1", 1, 1),
            Lesson("Pregunta Final 2", 0, 1)
        )
    )

    init {
        // Carga los módulos iniciales cuando se crea el ViewModel.
        _modules.value = listOf(
            Module(1, "Modulo 1", "Fundamentos del lenguaje de señas mexicano", 0.75f, true),
            Module(2, "Modulo 2", "Abecedario y números", 0.2f, true),
            Module(3, "Modulo 3", "Saludos y presentaciones", 0.5f, true),
            Module(4, "Modulo 4", "Preguntas comunes", 0.9f, true)
        )
    }

    /** Devuelve un módulo específico basado en su ID. */
    fun getModule(moduleId: Int?): Module? {
        return _modules.value.find { it.id == moduleId }
    }

    /** Devuelve la lista de lecciones para un módulo específico. */
    fun getLessonsForModule(moduleId: Int?): List<Lesson> {
        return allLessons[moduleId] ?: emptyList()
    }
}
