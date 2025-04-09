package com.example.statstalkerback.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var nom: String = "",
    var prenom: String = "",
    var mail: String = "",
    var pseudo: String = "",
    var password: String = ""
)