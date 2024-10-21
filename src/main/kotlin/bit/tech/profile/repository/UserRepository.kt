package bit.tech.profile.repository

import bit.tech.profile.models.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Profile, Long> {
    fun findUserByEmail(email: String): Profile
}
