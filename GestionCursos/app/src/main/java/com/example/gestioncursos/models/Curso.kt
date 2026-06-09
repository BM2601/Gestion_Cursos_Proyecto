package com.example.gestioncursos.models

data class Curso(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val profesorId: Int = 0,
    val nombreProfesor: String = "",
    val categoria: String = "",
    val activo: Boolean = true,
    val duracion: String = "",
    val nivel: String = ""
)
