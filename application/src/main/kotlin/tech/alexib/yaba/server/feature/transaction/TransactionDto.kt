@file:UseSerializers(UUIDSerializer::class, JavaLocalDateSerializer::class)

package tech.alexib.yaba.server.feature.transaction

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.JavaLocalDateSerializer
import tech.alexib.yaba.domain.common.UUIDSerializer
import java.time.LocalDate
import java.util.UUID

@Serializable
@GraphQLName("Transaction")
data class TransactionDto(
    val accountId: UUID,
    val itemId: UUID,
    val category: String? = null,
    val subcategory: String? = null,
    val type: String,
    val name: String,
    val amount: Double,
    val userId: UUID,
    val isoCurrencyCode: String? = "USD",
    val unofficialCurrencyCode: String? = "",
    val date: LocalDate,
    val pending: Boolean,
    val id: UUID,
    val merchantName: String? = null
)
