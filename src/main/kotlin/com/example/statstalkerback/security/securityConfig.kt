package com.example.statstalkerback.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/statstalker/signup", "/statstalker/login","/statstalker/delete").permitAll() // Autoriser l'inscription sans authentification
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() } // Nouvelle façon de désactiver CSRF
            .formLogin { it.disable() }
        return http.build()
    }
//add comment
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}