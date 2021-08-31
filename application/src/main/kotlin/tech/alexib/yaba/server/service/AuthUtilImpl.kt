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
    override fun generateToken(userId: UserId, email: Email) =
        jwtService.accessToken(userId, email)

    override fun passwordsMatch(plainPassword: String, encodedPassword: String): Boolean = passwordEncoder.matches(
        plainPassword,
        encodedPassword
    )
}
