package com.example.template2025.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<SignupResponse>

    companion object {
        // 👉 Emulador Android → backend en tu Mac
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"
        // Si usas dispositivo físico, cambia por la IP de tu Mac, ej:
        // private const val DEFAULT_BASE_URL = "http://192.168.1.8:8000/"

        fun create(baseUrl: String = DEFAULT_BASE_URL): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }
    }
}
