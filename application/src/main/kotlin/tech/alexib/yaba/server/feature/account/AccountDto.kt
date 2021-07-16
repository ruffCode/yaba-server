@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.server.feature.account

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.transaction.TransactionDto
import java.util.UUID

@Serializable
@GraphQLName("Account")
data class AccountDto(
    @Contextual
    val id: UUID,
    @Contextual
    val itemId: UUID,
    val plaidAccountId: String,
    val name: String,
    val mask: String,
    val officialName: String? = null,
    val currentBalance: Double,
    val availableBalance: Double? = null,
    val creditLimit: Double? = null,
    val isoCurrencyCode: String? = null,
    val unofficialCurrencyCode: String? = null,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false

) {
    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var item: ItemDto

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var transactions: List<TransactionDto>
}

@Serializable
enum class AccountType {
    DEPOSITORY,
    CREDIT,
    INVESTMENT,
    LOAN
}

@Serializable
enum class AccountSubtype {
    CHECKING,
    SAVINGS,
    CD,
    CREDIT_CARD,
    MONEY_MARKET,
    IRA,
    FOUR_HUNDRED_ONE_K,
    STUDENT,
    MORTGAGE
}
