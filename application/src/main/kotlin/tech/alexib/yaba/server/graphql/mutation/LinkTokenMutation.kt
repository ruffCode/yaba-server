package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.util.serverError
import tech.alexib.yaba.server.util.unauthorized
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Component
class LinkTokenMutation(
    private val plaidService: PlaidService,
    private val itemRepository: ItemRepository
) : Mutation {
    @Authenticated
    suspend fun createLinkToken(context: YabaGraphQLContext,  itemId: UUID? = null): LinkTokenResult {
        val userId = context.userId ?: unauthorized()
        val accessToken: String? = itemId?.let { id ->
            itemRepository.findById(id.itemId()).fold({
                null
            }, { it.accessToken })
        }
        return plaidService.createLinkToken(userId, accessToken).fold({
            logger.error { it }
            serverError("Error creating link token")
        }, {
            LinkTokenResult(it.linkToken)
        })
    }
}

@GraphQLName("LinkToken")
data class LinkTokenResult(
    val linkToken: String
)
