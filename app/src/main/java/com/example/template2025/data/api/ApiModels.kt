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

data class LoginResponse(
    val access_token: String,
    val token_type: String = "bearer"
)
