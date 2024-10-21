package bit.tech.profile.controllers

import bit.tech.profile.models.Profile
import bit.tech.services.JwtTokenProvider
import bit.tech.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController // used to create rest api
@RequestMapping("api/users") // To map the urls to controller methods
class UserController(private val userService: UserService, private val jwtTokenProvider: JwtTokenProvider) {

    @PostMapping("/create") // added to the security config to permit without token
    fun createUser(@RequestBody user: Profile): ResponseEntity<Profile> {
        return ResponseEntity.ok(userService.createUser(user))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: Profile): ResponseEntity<Profile> {
        return ResponseEntity.ok(userService.updateUser(id, user))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Optional<Profile>> {
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @PostMapping("/signup") // added to the security config to permit without token
    fun signupUser(@RequestBody user: Profile): ResponseEntity<String> {
        userService.createUser(user)
        return ResponseEntity.ok("User signed up successfully")
    }

    @PostMapping("/login") // added to the security config to permit without token
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<String> {
        val user = userService.findUserByEmailId(loginRequest.email)
        return if (user != null && loginRequest.password == user.password) {
            val token =
                jwtTokenProvider.generateToken(UsernamePasswordAuthenticationToken(user.email, null, emptyList()))
            userService.updateJwtToken(user.email, token)
            ResponseEntity.ok(token)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password")
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestHeader("Authorization") token: String): ResponseEntity<String> {
        val actualToken = token.removePrefix("Bearer ")
        if (jwtTokenProvider.validateToken(actualToken)) {
            val authentication = jwtTokenProvider.getAuthentication(actualToken)
            val newToken = jwtTokenProvider.generateToken(authentication)
            userService.updateJwtToken(authentication.name, newToken)
            return ResponseEntity.ok(newToken)
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token")
        }
    }
}

data class LoginRequest(val email: String, val password: String)
