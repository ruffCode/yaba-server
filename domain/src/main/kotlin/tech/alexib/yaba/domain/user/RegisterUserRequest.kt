package tech.alexib.yaba.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(
    val email: Email,
    val password: String
)
