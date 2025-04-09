package com.example.statstalkerback.apiRest

import com.example.statstalkerback.bean.DeleteBean
import com.example.statstalkerback.bean.LoginBean
import com.example.statstalkerback.model.User
import com.example.statstalkerback.services.JwtService
import com.example.statstalkerback.services.PasswordService
import com.example.statstalkerback.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/statstalker")
class StatStalkerAPI(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val jwtService: JwtService
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



    @PostMapping("/delete")
    fun deleteUser(@RequestBody deleteBean: DeleteBean): ResponseEntity<String> {
        println("Reçu suppression pour: ${deleteBean.pseudo}")



        return if (userService.deleteUser(deleteBean)) {
            ResponseEntity.ok("Utilisateur supprimé")
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pseudo ou mot de passe incorrect")
        }
    }
// Un test rapide
    @PostMapping("/test")
    fun test(@RequestBody message: String ): ResponseEntity<String>{
        println("C'est la fête")
        return ResponseEntity.ok("C'est la fête 2")
    }


}




