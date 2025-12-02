package com.example.template2025.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2    .http.POST

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/home")
    suspend fun getHomeData(): HomeResponse

    companion object {
        fun create(baseUrl: String): ApiService {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            return Retrofit.Builder()
                .baseUrl(baseUrl) // ej. http://10.0.2.2:8000/
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }

    }
    object RetrofitClient {
        private const val BASE_URL = "https://androidbackend-production-1dbe.up.railway.app/"

        // 🚨 Necesitas inyectar o inicializar tu AuthRepository primero (e.g., en tu Application)
        lateinit var authRepository: AuthRepository

        // Crea el cliente HTTP una vez que el AuthRepository esté disponible
        private val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                // Pasamos la función que obtiene el token de forma síncrona
                .addInterceptor(AuthInterceptor { authRepository.getAuthToken() })
                .build()
        }
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Instancia del ApiService
        val apiService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}
