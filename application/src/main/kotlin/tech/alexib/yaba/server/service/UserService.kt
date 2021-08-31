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

import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.common.AuthUtil
import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.LoginRequest
import tech.alexib.yaba.domain.user.RegisterUserCommand
import tech.alexib.yaba.domain.user.RegisterUserRequest
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.UserLoginError
import tech.alexib.yaba.domain.user.UserRegistrationError
import tech.alexib.yaba.domain.user.ValidUserRegistration
import tech.alexib.yaba.domain.user.login
import tech.alexib.yaba.domain.user.register
import tech.alexib.yaba.domain.user.validate
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.feature.user.UserEntity
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.toDomain
import tech.alexib.yaba.server.feature.user.toDto
import tech.alexib.yaba.server.util.badRequest
import tech.alexib.yaba.server.util.serverError
import tech.alexib.yaba.server.util.unauthorized

interface UserService {
    suspend fun login(loginRequest: LoginRequest): User
    suspend fun register(request: RegisterUserRequest): User
    suspend fun findByEmail(email: String): UserDto
    suspend fun isUserActive(id: UserId): Boolean
    suspend fun findById(id: UserId): User?
    fun addToken(user: UserDto): UserDto
}

private val logger = KotlinLogging.logger {}

@Component
class UserServiceImpl(private val authUtil: AuthUtil, private val userRepository: UserRepository) : UserService {

    override suspend fun login(loginRequest: LoginRequest): User {
        return loginRequest.login(
            ::findByEmail,
            authUtil::generateToken,
            authUtil::passwordsMatch
        ).fold({
            when (it) {
                is UserLoginError.NotFound -> {
                    logger.error { "User not found ${loginRequest.email}" }
                }
                is UserLoginError.InvalidCredentials -> logger.error { "Invalid credentials" }
            }
            unauthorized()
        }, {
            userRepository.setLastLogin(it.id)
            it
        })
    }

    private suspend fun findByEmail(email: Email): User? = userRepository.findByEmail(email.value).fold({
        logger.error { it.message }
        null
    }, {
        it.toDomain()
    })

    private suspend fun existsByEmail(email: Email): Boolean =
        userRepository.findByEmail(email.value).fold({ false }, { true })

    private suspend fun createUser(validUserRegistration: ValidUserRegistration): User {
        return userRepository.createUser(
            UserEntity(
                validUserRegistration.id.value,
                validUserRegistration.email.value,
                validUserRegistration.password
            )
        ).fold({
            logger.error { it.message }
            serverError("Unknown Registration Error")
        }, {
            it.toDomain().copy(token = validUserRegistration.token)
        })
    }

    override suspend fun register(request: RegisterUserRequest): User {
        return RegisterUserCommand(request).register(
            ::createUser
        ) {
            it.validate(
                ::existsByEmail,
                authUtil::encodePassword,
                authUtil::generateToken
            )
        }.fold({ error ->
            when (error) {
                UserRegistrationError.PasswordTooShort -> badRequest(
                    "Password too short - must be at least 12 characters"
                )
                UserRegistrationError.DuplicateEmail -> badRequest(
                    "User with email ${request.email.value} already registered"
                )
                UserRegistrationError.InvalidEmail -> badRequest("Invalid email: ${request.email.value}")
            }
        }, {
            it
        })
    }

    override suspend fun isUserActive(id: UserId): Boolean {
        return userRepository.isUserActive(id)
    }

    override suspend fun findByEmail(email: String): UserDto =
        userRepository.findByEmail(email).fold({ badRequest("User not found") }, { it.toDomain().toDto() })

    override fun addToken(user: UserDto): UserDto =
        user.copy(token = authUtil.generateToken(UserId(user.id), Email(user.email)))

    override suspend fun findById(id: UserId): User? {
        return userRepository.findById(id).fold({ null }, { it.toDomain() })
    }
}
