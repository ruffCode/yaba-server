package tech.alexib.yaba.server.dto

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.entity.LinkEventEntity

import java.util.UUID

@GraphQLName("LinkEvent")
@Serializable
data class LinkEventDto(
    @Contextual
    val id: UUID = UUID.randomUUID(),
    val type: String,
    val userId: UserId,
    val linkSessionId: String,
    val requestId: String? = null,
    val errorType: String? = null,
    val errorCode: String? = null,
) {
    fun toEntity(): LinkEventEntity = LinkEventEntity(
        id = id,
        type = type,
        userId = userId.value,
        linkSessionId = linkSessionId,
        requestId = requestId,
        errorType = errorType,
        errorCode = errorCode
    )
}

@Serializable
data class LinkEventInput(
    val type: String,
    val linkSessionId: String,
    val requestId: String? = null,
    val errorType: String? = null,
    val errorCode: String? = null,
){
    fun toEntity(userId: UserId) = LinkEventEntity(
        id = UUID.randomUUID(),
        type = type,
        userId = userId.value,
        linkSessionId = linkSessionId,
        requestId = requestId,
        errorType = errorType,
        errorCode = errorCode
    )
}
