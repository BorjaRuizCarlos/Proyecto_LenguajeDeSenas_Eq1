package com.example.template2025.data

import com.example.template2025.data.api.ApiService
import com.example.template2025.data.api.SignupRequest
import com.example.template2025.data.api.SignupResponse

class AuthRepository(private val api: ApiService) {
    suspend fun signup(nombre: String, correo: String, contrasena: String): Result<SignupResponse> {
        return try {
            val res = api.signup(SignupRequest(nombre, correo, contrasena))
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                val msg = res.errorBody()?.string().orEmpty().ifBlank { "Error al registrar" }
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
