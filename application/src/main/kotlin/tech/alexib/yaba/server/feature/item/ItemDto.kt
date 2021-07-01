package tech.alexib.yaba.server.feature.item

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.dto.InstitutionDto
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.transaction.TransactionDto

import java.util.UUID

@GraphQLName("Item")
@Serializable
data class ItemDto(
    @Contextual
    val id: UUID,
    val userId: UserId,
    val plaidInstitutionId: String,
    val status: String,
) {
    @Transient
    @kotlinx.serialization.Transient
    lateinit var institution: InstitutionDto

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var transactions: List<TransactionDto>

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var accounts: List<AccountDto>

}
