package com.example.gestioncursos.models

data class Material(
    val id: Int = 0,
    val cursoId: Int,
    val nombreArchivo: String,
    val rutaArchivo: String,
    val fechaSubida: String
)
