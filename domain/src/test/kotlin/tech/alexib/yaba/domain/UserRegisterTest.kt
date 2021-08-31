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
package tech.alexib.yaba.domain

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import tech.alexib.yaba.domain.UserStubs.existsByEmail
import tech.alexib.yaba.domain.common.AuthUtil
import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.ExistsByEmail
import tech.alexib.yaba.domain.user.RegisterUserCommand
import tech.alexib.yaba.domain.user.RegisterUserRequest
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.UserRegistrationError
import tech.alexib.yaba.domain.user.isValid
import tech.alexib.yaba.domain.user.register
import tech.alexib.yaba.domain.user.validate

class UserRegisterTest {

    private val userRegistration = RegisterUserRequest(Email("alexi@aol.com"), "password1234")
    private val badUserRegistration = RegisterUserRequest(Email("alexi@aol.com"), "password")

    private val authUtilStub = AuthUtilStub()

    @Test
    fun `validates email`() {
        val validEmails =
            listOf("alexi@aol.com", "alexi.alexi@aol.com", "alexi_alexi@aol.co.com", "alexi+alexi@aol.com")

        val invalidEmails = listOf("alexi.com", "@a.org", "alexi@@aol.com")

        validEmails.map { Email(it) }.forEach {
            println(it.value)
            it.isValid().shouldBeTrue()
        }
        invalidEmails.map { Email(it) }.forEach {
            it.isValid().shouldBeFalse()
        }
    }

    @Test
    fun `registers user`() {
        val command = RegisterUserCommand(userRegistration)

        runBlocking {
            command.register(
                {
                    User(id = it.id, email = it.email.value, password = it.password)
                }
            ) {
                it.validate(
                    existsByEmail(false),
                    authUtilStub::encodePassword,
                    authUtilStub::generateToken
                )
            }.fold(
                { fail { "Expected success" } },
                { }
            )
        }
    }

    @Test
    fun `fails to register user`() {
        val command = RegisterUserCommand(badUserRegistration)

        runBlocking {
            command.register(
                {
                    User(id = it.id, email = it.email.value, password = it.password)
                }
            ) {
                it.validate(
                    existsByEmail(false),
                    authUtilStub::encodePassword,
                    authUtilStub::generateToken
                )
            }
                .fold(
                    { it.shouldBeSameInstanceAs(UserRegistrationError.PasswordTooShort) },
                    { fail { "Expected left" } }
                )
        }
    }

    @Test
    fun `successfully validates user registration`() {
        runBlocking {
            userRegistration.validate(
                { false },
                authUtilStub::encodePassword,
                authUtilStub::generateToken
            ).fold({
                fail { "Expected valid user registration" }
            }, {
            })
        }
    }

    @Test
    fun `fails to validate user registration`() {
        runBlocking {
            userRegistration.validate(
                { true },
                authUtilStub::encodePassword,
                authUtilStub::generateToken
            ).fold({
                it.shouldBeSameInstanceAs(UserRegistrationError.DuplicateEmail)
            }, {
                fail { "Expected ${UserRegistrationError.DuplicateEmail}" }
            })
        }
    }
}

private class AuthUtilStub : AuthUtil {
    override fun encodePassword(password: String): String = password.reversed()

    override fun generateToken(userId: UserId, email: Email): String =
        "${userId.value}:${email.value}"

    override fun passwordsMatch(plainPassword: String, encodedPassword: String): Boolean =
        plainPassword == encodedPassword
}

object UserStubs {

    fun existsByEmail(exists: Boolean) = ExistsByEmail { exists }
}
