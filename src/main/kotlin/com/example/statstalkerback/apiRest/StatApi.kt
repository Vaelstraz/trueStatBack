package com.example.statstalkerback.apiRest

import com.example.statstalkerback.bean.LoginBean
import com.example.statstalkerback.model.User
import com.example.statstalkerback.services.JwtService
import com.example.statstalkerback.services.PasswordService
import com.example.statstalkerback.services.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/statstalker")
class StatStalkerAPI(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/signup")
    fun createAccount(@RequestBody user:User): ResponseEntity<Map<String, String>> {

        val hashedPassword = passwordService.hashPassword(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)

        println("Nom : ${user.nom}")
        println("Prenom: ${user.prenom}")
        println("Mail:${user.mail}")
        println("Pseudo: ${user.pseudo}")
        println("Password: ${hashedPassword}")

        userService.saveUser(userWithHashedPassword)

        println("Enregistrement réussi")

        val token = jwtService.generateToken(user.pseudo)

        return ResponseEntity.ok(
            mapOf(
            "message" to "Compte utilisateur créé",
                "token" to token
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody loginBean: LoginBean): ResponseEntity<Map<String, String>> {
        val pseudo = loginBean.pseudo
        val password = loginBean.password

        val user = userService.authenticateUser(pseudo, password)
        println("Utilisateur trouvé: ${user?.pseudo}")
        println("Mot de passe en BDD (hash): ${user?.password}")

        return if (user != null) {
            println("Mot de passe: OK")

            val token = jwtService.generateToken(user.pseudo)

            ResponseEntity.ok(
                mapOf(
                    "message" to "Connexion réussie",
                    "token" to token
                )
            )
        } else {
            println("Echec de l'authentification")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                mapOf("message" to "Pseudo ou mot de passe incorrect")
            )
        }
    }


    @DeleteMapping("/users/{pseudo}")
    fun deleteUser(
        @PathVariable pseudo: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val authHeader = request.getHeader("Authorization")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token manquant")

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Format du token invalide")
        }

        val token = authHeader.substring(7)
        val tokenPseudo = jwtService.extractUsername(token)

        if (tokenPseudo != pseudo) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ce n'est pas votre compte")
        }

        val deleted = userService.deleteByPseudo(pseudo)

        return if (deleted) {
            ResponseEntity.ok("Utilisateur supprimé")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé")
        }
    }

    @GetMapping("/me")
    fun getUserInfoFromToken(request: HttpServletRequest): ResponseEntity<User> {
        val authHeader = request.getHeader("Authorization") ?: return ResponseEntity.status(401).build()
        if (!authHeader.startsWith("Bearer ")) return ResponseEntity.status(401).build()

        val token = authHeader.substring(7)
        val pseudo = jwtService.extractUsername(token)

        val user = userService.getUserByPseudo(pseudo) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(user)
    }


    @PutMapping("/users/{pseudo}")
    fun updateUser(
        @PathVariable pseudo: String,
        @RequestBody updatedUser: User,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val authHeader = request.getHeader("Authorization") ?: return ResponseEntity.status(401).body("Token manquant")
        if (!authHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body("Format du token invalide")

        val token = authHeader.substring(7)
        val tokenPseudo = jwtService.extractUsername(token)

        if (tokenPseudo != pseudo) {
            return ResponseEntity.status(403).body("Ce n'est pas votre compte")
        }

        val existingUser = userService.getUserByPseudo(pseudo)
            ?: return ResponseEntity.status(404).body("Utilisateur non trouvé")

        val userToSave = existingUser.copy(
            pseudo = updatedUser.pseudo,
            password = passwordEncoder.encode(updatedUser.password)
        )

        userService.saveUser(userToSave)
        return ResponseEntity.ok("Utilisateur mis à jour")
    }


}




