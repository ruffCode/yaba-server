@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.server.feature.user

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import tech.alexib.yaba.domain.user.UserRole
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.transaction.TransactionDto
import java.util.UUID


@GraphQLName("User")
@Serializable
data class UserDto(
    @Contextual
    val id: UUID,
    val email: String,
    val token: String? = null,
    @GraphQLIgnore
    val role: UserRole = UserRole.USER
) {
    @Transient
    @kotlinx.serialization.Transient
    lateinit var items: List<ItemDto>

    @Transient
    @kotlinx.serialization.Transient
    lateinit var accounts: List<AccountDto>

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var transactions: List<TransactionDto>
}
