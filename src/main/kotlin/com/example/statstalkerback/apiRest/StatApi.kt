package com.example.statstalkerback.apiRest

import com.example.statstalkerback.bean.DeleteBean
import com.example.statstalkerback.bean.LoginBean
import com.example.statstalkerback.model.User
import com.example.statstalkerback.model.UserCredentials
import com.example.statstalkerback.repository.UserRepository
import com.example.statstalkerback.services.PasswordService
import com.example.statstalkerback.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/statstalker")
class StatStalkerAPI(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val userRepository: UserRepository
) {

    @PostMapping("/signup")
    fun createAccount(@RequestBody user:User): ResponseEntity<String> {



        val hashedPassword = passwordService.hashPassword(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)

        println("Nom : ${user.nom}")
        println("Prenom: ${user.prenom}")
        println("Mail:${user.mail}")
        println("Pseudo: ${user.pseudo}")
        println("Password: ${hashedPassword}")

        userService.saveUser(userWithHashedPassword)

        println("Enregistrement réussi")

        return ResponseEntity.ok("Compte utilisateur créé avec succès ! ")
    }

    @PostMapping("/login")
    fun login(@RequestBody loginBean: LoginBean): ResponseEntity<String> {
        val pseudo = loginBean.pseudo
        val password = loginBean.password

        val user = userService.authenticateUser(pseudo, password)
        println("Utilisateur trouvé: ${user?.pseudo}")
        println("Mot de passe en BDD (hash): ${user?.password}")

        return if (user != null) {
            println("Mot de passe: OK")
            ResponseEntity.ok("Connexion réussie")
        } else {
            println("Echec de l'authentification")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pseudo ou mot de passe incorrect")
        }
    }

    @DeleteMapping("/delete")
    fun deleteUser(@RequestBody userCredentials: UserCredentials): ResponseEntity<String> {
        println("Reçu suppression pour: ${userCredentials.pseudo}")
        val pseudo = userCredentials.pseudo
        val password = userCredentials.password

        return if (userService.deleteUser(userCredentials)) {
            ResponseEntity.ok("Utilisateur supprimé")
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pseudo ou mot de passe incorrect")
        }
    }


}




