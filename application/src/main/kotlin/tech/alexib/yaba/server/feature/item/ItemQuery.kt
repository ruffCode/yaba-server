package tech.alexib.yaba.server.feature.item

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.util.notFound
import java.util.UUID

@Component
class ItemQuery(
    private val itemRepository: ItemRepository
) : Query {

    @Authenticated
    @GraphQLDescription("Returns all of user's items")
    suspend fun itemsByUser(context: YabaGraphQLContext): List<ItemDto> =
        itemRepository.findByUserId(context.id()).map {
            it.toDto()
        }.toList()

    @Authenticated
    suspend fun itemById(itemId: UUID): ItemDto {
        return itemRepository.findById(itemId.itemId()).fold({
            notFound("No item found with id: $itemId")
        }, {
            it.toDto()
        })
    }
}
