package com.example.template2025.data

import com.example.template2025.data.api.*

class AuthRepository(private val api: ApiService) {

    suspend fun signup(nombre: String, correo: String, contrasena: String): Result<SignupResponse> =
        try {
            val res = api.signup(SignupRequest(nombre, correo, contrasena))
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(IllegalStateException(res.errorBody()?.string().orEmpty().ifBlank { "Error al registrar" }))
        } catch (e: Exception) {
            Result.failure(e)
        }

    // AuthRepository.kt
    suspend fun login(correo: String, contrasena: String): Result<LoginResponse> {
        return try {
            val res = api.login(LoginRequest(correo, contrasena))
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(IllegalStateException(res.errorBody()?.string().orEmpty()
                .ifBlank { "Error al iniciar sesión" }))
        } catch (e: Exception) { Result.failure(e) }
    }
}
