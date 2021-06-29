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
