package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("Usuario") val usuario: Map<String, Any>?,
    @SerializedName("Dias") val dias: Map<String, Boolean>?,

    @SerializedName("Mision1") val mision1: Int?,
    @SerializedName("Mision2") val mision2: Int?,
    @SerializedName("Mision3") val mision3: Int?,

    // Ahora como Double porque el backend manda decimal
    @SerializedName("Progreso") val progreso: Double?,

    @SerializedName("Racha") val racha: Int?,

    @SerializedName("ProgresoModulo1") val progresoModulo1: Int?,
    @SerializedName("ProgresoModulo2") val progresoModulo2: Int?,
    @SerializedName("ProgresoModulo3") val progresoModulo3: Int?
)
