package com.example.statstalkerback.services

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService{
    private val passwordEncoder : PasswordEncoder = BCryptPasswordEncoder()

    fun hashPassword(password:String):String{
        return passwordEncoder.encode(password)
    }

    fun checkPassword(rawPassword: String, hashedPassword: String): Boolean{
        return passwordEncoder.matches(rawPassword, hashedPassword)
    }
}