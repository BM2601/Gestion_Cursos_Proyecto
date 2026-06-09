package com.example.gestioncursos.models

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val rol: String,
    val password: String = ""
)