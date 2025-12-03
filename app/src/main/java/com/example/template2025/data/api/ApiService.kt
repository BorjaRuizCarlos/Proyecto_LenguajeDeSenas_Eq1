// ApiService.kt
package com.example.template2025.data.api

import com.example.template2025.viewModel.UserProfile
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ===================== AUTH =====================

    @POST("auth/signup")
    suspend fun signup(
        @Body body: SignupRequest
    ): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    // ===================== HOME =====================

    @GET("api/home")
    suspend fun getHomeData(
        @Header("Authorization") authorization: String
    ): Response<HomeResponse>

    // ===================== MODULOS =====================

    @GET("api/modulos/")
    suspend fun getModuloData(
        @Header("Authorization") authorization: String
    ): Response<ModuloResponse>

    @GET("api/modulos/{modulo_id}/lecciones")
    suspend fun getLeccionesData(
        @Header("Authorization") authorization: String,
        @Path("modulo_id") moduloId: Int
    ): Response<LeccionesResponse>

    @GET("lessons/{leccion_id}")
    suspend fun getLessonContent(
        @Header("Authorization") authorization: String,
        @Path("leccion_id") leccionId: Int
    ): Response<LessonContentResponse>

    @POST("lessons/{leccion_id}/answer")
    suspend fun sendLessonAnswer(
        @Header("Authorization") authorization: String,
        @Path("leccion_id") lessonId: Int,
        @Body body: LessonAnswerRequest
    ): Response<Unit> // Usamos Response<Unit> si la respuesta no tiene cuerpo.
    // ===================== PERFIL =====================

    @GET("profile/me")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): Response<UserProfile>

    // ===================== DICCIONARIO =====================

    // GET /dictionary/  (search es opcional)
    @GET("dictionary/")
    suspend fun getDictionary(
        @Header("Authorization") authorization: String,
        @Query("search") search: String? = null
    ): Response<DictionaryListResponse>

    // GET /dictionary/{word_id}
    @GET("dictionary/{word_id}")
    suspend fun getDictionaryWord(
        @Header("Authorization") authorization: String,
        @Path("word_id") wordId: Int
    ): Response<DictionaryWordDetail>

    // ===================== MISIONES DIARIAS =====================

    // GET /missions/daily
    @GET("missions/daily")
    suspend fun getDailyMissions(
        @Header("Authorization") authorization: String
    ): Response<DailyMissionsResponse>

    // POST /missions/update
    @POST("missions/update")
    suspend fun updateMission(
        @Header("Authorization") authorization: String,
        @Body body: UpdateMissionRequest
    ): Response<Unit>

    // ===================== CREACIÓN DE RETROFIT =====================

    companion object {
        fun create(baseUrl: String): ApiService {
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

    object RetrofitClient {
        private const val BASE_URL =
            "https://androidbackend-production-1dbe.up.railway.app/"

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val apiService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}
