package com.example.statstalkerback.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService {
    private val secretKey = "maSuperCleSecretePourJWTQuiFait256Bits!" // minimum 32 caractères

    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 1000 * 60 * 60 * 24) // 24h

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey.toByteArray())
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}
