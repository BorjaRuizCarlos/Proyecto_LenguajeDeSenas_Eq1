package com.example.template2025.data.api

// Respuesta de GET /dictionary/
data class DictionaryListResponse(
    val total: Int,
    val palabras: List<DictionaryWord>
)

// Cada palabra dentro de "palabras"
data class DictionaryWord(
    val id: Int,
    val titulo: String,
    val url: String,
    val duracion_seg: Int,
    val leccion: String,
    val modulo: String
)

// Respuesta de GET /dictionary/{word_id}
data class DictionaryWordDetail(
    val id: Int,
    val titulo: String,
    val url: String,
    val duracion_seg: Int,
    val leccion_id: Int,
    val leccion_nombre: String,
    val modulo_id: Int,
    val modulo_nombre: String
)
