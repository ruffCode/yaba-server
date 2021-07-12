@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.server.feature.transaction

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import java.util.UUID

@Serializable
@GraphQLName("TransactionsUpdated")
data class TransactionsUpdatedDto(
    val added: List<TransactionDto>? = null,
    val removed: List<UUID>? = null
)
