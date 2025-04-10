package com.example.statstalkerback.services

//import com.example.statstalkerback.model.UserCredentials
import com.example.statstalkerback.model.User
import com.example.statstalkerback.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // Enregistre le nouvel utilisateur
    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    // Vérifie si l'utilisateur existe
    fun authenticateUser(pseudo: String, password: String): User? {
        val user = userRepository.findByPseudo(pseudo)

        return if (user != null && passwordEncoder.matches(password, user.password)) {
            user
        } else {
            null
        }
    }
    //Supprime l'utilisateur
    fun deleteByPseudo(pseudo: String): Boolean {
        val user = userRepository.findByPseudo(pseudo)
        return if (user != null) {
            userRepository.delete(user)
            true
        } else {
            false
        }
    }
}
