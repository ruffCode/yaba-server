/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
class JWTService(jwtConfig: JwtConfig) {

    private val secret = jwtConfig.secret

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
        private val defaultRoles = arrayOf(SimpleGrantedAuthority("USER").toString())
    }
}
