package tech.alexib.yaba.server.plaid.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkMetadata(
    val accounts: List<LinkAccount>,
    val institution: LinkInstitution,
    @SerialName("link_session_id")
    val linkSessionId: String,
    @SerialName("public_token")
    val publicToken: String
)

@Serializable
data class LinkInstitution(
    @SerialName("institution_id")
    val institutionId: String,
    val name: String
)

@Serializable
data class LinkAccount(
    val id: String,
    val mask: String,
    val name: String,
    val subtype: String,
    val type: String
)
