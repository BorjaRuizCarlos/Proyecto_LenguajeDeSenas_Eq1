// app/src/main/java/com/example/template2025/data/api/LoginResponse.kt
package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    // el JSON trae "token": "xxxxx"
    @SerializedName("token")
    val token: String,

    // Si quieres usar los datos del usuario luego, ya están listos:
    @SerializedName("usuario")
    val usuario: UsuarioResponse
)

data class UsuarioResponse(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("creado_en")
    val creadoEn: String,

    @SerializedName("es_admin")
    val esAdmin: Boolean,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("monedas")
    val monedas: Int
)
