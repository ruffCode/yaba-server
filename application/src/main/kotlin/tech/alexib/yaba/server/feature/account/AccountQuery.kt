package tech.alexib.yaba.server.feature.account

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import java.util.UUID

@Component
class AccountQuery(
    private val accountRepository: AccountRepository
) : Query {

    @Authenticated
    @GraphQLDescription("Returns all of user's accounts")
    suspend fun accountsByUser(context: YabaGraphQLContext): List<AccountDto> =
        accountRepository.findByUserId(context.id()).map { it.toDto() }.toList()

    @Authenticated
    suspend fun accountsByItemId(itemId: UUID): List<AccountDto> =
        accountRepository.findByItemId(itemId.itemId()).map { it.toDto() }.toList()

    @Authenticated
    suspend fun accountById(id: UUID): AccountDto =
        accountRepository.findById(id).toDto()
}
