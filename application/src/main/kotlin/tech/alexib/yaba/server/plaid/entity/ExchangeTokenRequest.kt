package tech.alexib.yaba.server.plaid.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeTokenRequest(
    @SerialName("client_id")
    val clientId: String,
    val secret: String,
    @SerialName("public_token")
    val publicToken: String
)
