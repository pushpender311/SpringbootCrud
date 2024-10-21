package bit.tech.services

import bit.tech.profile.models.Profile
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration}")
    private var jwtExpiration: Int = 0

    fun generateToken(authentication: Authentication): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration * 1000)
        return Jwts.builder()
            .setSubject(authentication.name)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: Exception) {
            // Log the exception or handle it as needed
        }
        return false
    }


    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
        val profile = Profile(id = 0, firstName = "", lastName = "", email = claims.subject, password = "")
        return UsernamePasswordAuthenticationToken(profile, "", emptyList())
    }
}
