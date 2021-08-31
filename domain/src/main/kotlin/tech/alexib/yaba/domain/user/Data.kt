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
@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.domain.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class User(
    val id: UserId = UserId(UUID.randomUUID()),
    val email: String,
    val password: String,
    val token: String? = null,
    val role: UserRole = UserRole.USER,
    val active: Boolean = true,
    @Contextual
    @SerialName("created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Contextual
    @SerialName("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    companion object
}

@Serializable
@JvmInline
value class UserId(val value: UUID)

@Serializable
@JvmInline
value class Email(val value: String)

fun Email.isValid(): Boolean = this.value.isNotEmpty() && this.value.matches(
    Regex("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
)

@Serializable
enum class UserRole {
    USER,
    ADMIN
}

fun UUID.userId() = UserId(this)

data class RegisterUserCommand(
    val data: RegisterUserRequest
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

data class ValidUserRegistration(
    val id: UserId,
    val email: Email,
    val password: String,
    val token: String
)
