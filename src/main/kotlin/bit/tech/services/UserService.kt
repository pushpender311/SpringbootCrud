package bit.tech.services

import bit.tech.profile.models.Profile
import bit.tech.profile.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun createUser(user: Profile): Profile {
        return userRepository.save(user)
    }

    fun deleteUser(id: Long) = userRepository.deleteById(id)
    fun getUserById(id: Long) = userRepository.findById(id)
    fun updateUser(id: Long, user: Profile): Profile {
        val existingUser = userRepository.findById(id).orElseThrow() { Exception("Profile not found") }
        return userRepository.save(
            existingUser.copy(
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.password,
                password = user.email
            )
        )
    }

    fun findUserByEmailId(email: String) = userRepository.findUserByEmail(email)

    fun updateJwtToken(email: String, token: String) {
        val user = userRepository.findUserByEmail(email)
        if (user != null) {
            user.jwtToken = token
            userRepository.save(user)
        }
    }

}