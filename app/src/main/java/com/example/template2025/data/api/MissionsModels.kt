package com.example.template2025.data.api

/**
 * Respuesta de GET /missions/daily
 */
data class DailyMissionsResponse(
    val fecha: String,
    val misiones: List<MissionRemote>
)

/**
 * Una misión individual tal como viene del backend.
 */
data class MissionRemote(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val progreso_actual: Int,
    val meta: Int,
    val completada: Boolean,
    val xp_recompensa: Int
)

/**
 * Body para POST /missions/update
 */
data class UpdateMissionRequest(
    val mision_id: Int,
    val progreso: Int
)



