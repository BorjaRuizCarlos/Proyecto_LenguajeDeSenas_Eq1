package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

data class LessonContentResponse(
    @SerializedName("id_leccion") val idLeccion: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("id_modulo") val idModulo: Int,
    @SerializedName("orden") val orden: Int,
    @SerializedName("completado") val completado: Boolean,
    @SerializedName("videos") val videos: List<VideoItem>?
)

data class VideoItem(
    @SerializedName("id_video") val id: Int = 0,
    @SerializedName("titulo") val nombre: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("duracion_seg") val duracionSeg: Int = 0,
    @SerializedName("orden") val orden: Int = 0
)
