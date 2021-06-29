package tech.alexib.yaba.server.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.common.AuthUtil
import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.security.JWTService

@Service
class AuthUtilImpl(private val passwordEncoder: PasswordEncoder, private val jwtService: JWTService) : AuthUtil {
    override fun encodePassword(password: String): String = passwordEncoder.encode(password)
    override fun generateToken(userId: UserId, email: Email) = jwtService.accessToken(userId, email)
    override fun passwordsMatch(plainPassword: String, encodedPassword: String): Boolean = passwordEncoder.matches(
        plainPassword,
        encodedPassword
    )
}
