package com.example.template2025.data.api

import com.google.gson.annotations.SerializedName

data class ModuloResponse(
    @SerializedName("modulos") val modulos: List<ModuloItem>?
)

data class ModuloItem(
    @SerializedName("name") val name: String,
    @SerializedName("current") val current: Int,
    @SerializedName("max") val max: Int,
    @SerializedName("id") val id: Int
)
