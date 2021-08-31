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

suspend inline fun LoginRequest.login(
    getUserByEmail: GetUserByEmail,
    generateToken: GenerateToken,
    passwordsMatch: PasswordMatches
): Either<UserLoginError, User> {
    val email = Email(this.email)
    val plainPassword = this.password

    return either {
        (
            getUserByEmail(email)?.let {
                if (passwordsMatch(plainPassword, it.password)) {
                    it.copy(token = generateToken(it.id, email)).right()
                } else UserLoginError.InvalidCredentials.left()
            } ?: UserLoginError.NotFound.left()
            ).bind()
    }
}

sealed class UserLoginError {
    object NotFound : UserLoginError()
    object InvalidCredentials : UserLoginError()
}
