package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

data class LeccionesResponse(
    @SerializedName("modulo_id") val moduloId: Int,
    @SerializedName("modulo_nombre") val moduloNombre: String,
    @SerializedName("lecciones") val lecciones: List<LeccionItem>?
)

data class LeccionItem(
    @SerializedName("name") val name: String,
    @SerializedName("current") val current: Int,
    @SerializedName("max") val max: Int,
    @SerializedName("id") val id: Int
)
