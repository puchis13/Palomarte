package com.example.palomarapp

data class Competencia(
    val grupoId: Int,  // Añadido el campo grupoId
    val nombreClub: String,
    val clasificacionVuelo: String,
    val ubicacionSuelta: String,
    val fechaCompetencia: String,
    val status: String,
    val kilometros: Int
)
