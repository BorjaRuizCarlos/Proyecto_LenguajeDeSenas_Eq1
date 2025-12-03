package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

// ====== SIGNUP ======
data class SignupRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String
)

data class SignupResponse(
    val id_usuario: Long?,
    val correo: String,
    val nombre: String,
    val creado_en: String?
)
data class LoginRequest(
    val correo: String,
    val contrasena: String
)




// ApiModels.kt (package com.example.template2025.data.api)
// ... (Tus clases SignupRequest, SignupResponse, LoginRequest, LoginResponse)

// ====== HOME ======
data class DayProgressResponse(val day: String, val completed: Boolean)
data class MissionResponse(val name: String, val current: Int, val max: Int)
data class ModuleResponse(val name: String, val current: Int, val max: Int)

// ====== MÓDULOS ======

/**
 * Representa un módulo individual de la respuesta de la API.
 * Corresponde a la estructura: { "name": "string", "current": 0, "max": 0, "id": 0 }
 */
data class ApiModule(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("current") val current: Int, // Progreso actual (ej. 30)
    @SerializedName("max") val max: Int // Total (ej. 50)
)

/**
 * Representa la respuesta completa del endpoint /api/modulos/.
 * Corresponde a la estructura: { "modulos": [...] }
 */
data class ModulesResponse(
    @SerializedName("modulos") val modulos: List<ApiModule>
)

// ====== LECCIONES (NUEVO) ======

data class ApiLeccion(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("current") val current: Int,
    @SerializedName("max") val max: Int
)
data class LessonAnswerRequest(
    @SerializedName("calificacion") val calificacion: String
)
/**
 * Estructura de la respuesta para /api/modulos/{modulo_id}/lecciones/
 * {
 * "modulo_id": 1,
 * "modulo_nombre": "Modulo 1: Fundamentos",
 * "lecciones": [...]
 * }
 */

// ====== MISIONES DIARIAS ======
