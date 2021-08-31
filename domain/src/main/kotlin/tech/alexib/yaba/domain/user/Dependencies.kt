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

fun interface ValidateUserRegistration {
    suspend operator fun invoke(req: RegisterUserRequest): Either<UserRegistrationError, ValidUserRegistration>
}

fun interface CreateUser {
    suspend operator fun invoke(user: ValidUserRegistration): User
}

fun interface GetUserByEmail {
    suspend operator fun invoke(email: Email): User?
}

fun interface ExistsByEmail {
    suspend operator fun invoke(email: Email): Boolean
}
fun interface EncodePassword {
    operator fun invoke(password: String): String
}
fun interface GenerateToken {
    operator fun invoke(userId: UserId, email: Email): String
}

fun interface PasswordMatches {
    operator fun invoke(password: String, encodedPassword: String): Boolean
}
