package com.example.palomarapp

data class Consumible(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val tipo: String,
    val fotografia: String // Fotografía en Base64
)
