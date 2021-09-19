/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        itemRepository.findByUserId(context.id(), false).map {
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
