package com.example.template2025.data.api

data class SignupRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String
)

data class UsuarioDTO(
    val id_usuario: Long,
    val correo: String,
    val nombre: String,
    val creado_en: String
)

data class SignupResponse(
    val token: String,
    val usuario: UsuarioDTO
)
