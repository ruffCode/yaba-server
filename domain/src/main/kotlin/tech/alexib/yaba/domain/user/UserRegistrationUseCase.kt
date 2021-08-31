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
package tech.alexib.yaba.domain.user

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import java.util.UUID

suspend inline fun RegisterUserRequest.validate(
    existsByEmail: ExistsByEmail,
    encodePassword: EncodePassword,
    generateToken: GenerateToken
): Either<UserRegistrationError, ValidUserRegistration> {
    val cmd = this
    return either {
        when {
            !cmd.email.isValid() -> UserRegistrationError.InvalidEmail.left()
            existsByEmail(cmd.email) -> UserRegistrationError.DuplicateEmail.left()
            cmd.password.length < 12 -> UserRegistrationError.PasswordTooShort.left()
            else -> {
                val id = UUID.randomUUID().userId()
                ValidUserRegistration(
                    id,
                    email = cmd.email,
                    password = encodePassword(cmd.password),
                    token = generateToken(id, email)
                ).right()
            }
        }.bind()
    }
}

sealed class UserRegistrationError {
    object DuplicateEmail : UserRegistrationError()
    object InvalidEmail : UserRegistrationError()
    object PasswordTooShort : UserRegistrationError()
}

suspend inline fun RegisterUserCommand.register(
    createUser: CreateUser,
    validateUser: ValidateUserRegistration
): Either<UserRegistrationError, User> {
    val cmd = this
    return either {
        val validUser = validateUser(cmd.data).bind()
        createUser(validUser)
    }
}
