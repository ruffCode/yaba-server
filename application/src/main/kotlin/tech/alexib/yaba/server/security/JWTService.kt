package tech.alexib.yaba.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.config.JwtConfig
import tech.alexib.yaba.server.feature.user.UserDto

@Service
class JWTService(private val jwtConfig: JwtConfig) {

    private val secret = jwtConfig.secret
    private val refresh = jwtConfig.refresh

    fun accessToken(userDto: UserDto): String = generate(userDto)

    fun accessToken(user: User): String = generate(user)

    fun accessToken(userId: UserId, email: Email): String = JWT.create()
        .withSubject(email.value)
        .withJWTId(userId.value.toString())
        .withArrayClaim("role", defaultRoles)
        .sign(Algorithm.HMAC512(secret.toByteArray()))

    fun decodeAccessToken(accessToken: String): DecodedJWT {
        return decode(secret, accessToken)
    }

    fun getRoles(decodedJWT: DecodedJWT) = decodedJWT.getClaim("role").asList(String::class.java)
        .map { SimpleGrantedAuthority(it) }

    private fun generate(user: UserDto): String {
        return JWT.create()
            .withSubject(user.email)
            .withJWTId(user.id.toString())
            .withArrayClaim("role", defaultRoles)
            .sign(Algorithm.HMAC512(secret.toByteArray()))
    }

    private fun generate(user: User): String = JWT.create()
        .withSubject(user.email)
        .withJWTId(user.id.value.toString())
        .withArrayClaim("role", defaultRoles)
        .sign(Algorithm.HMAC512(secret.toByteArray()))

    private fun decode(signature: String, token: String): DecodedJWT {
        return JWT.require(Algorithm.HMAC512(signature.toByteArray()))
            .build()
            .verify(token.replace("Bearer ", ""))
    }

    companion object {
        private val defaultRoles = arrayOf(SimpleGrantedAuthority("ROLE_USER").toString())
    }
}
