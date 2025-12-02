package com.example.template2025.data.api

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

