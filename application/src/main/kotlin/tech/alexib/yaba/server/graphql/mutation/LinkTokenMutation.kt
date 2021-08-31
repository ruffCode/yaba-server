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
package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.plaid.PlaidService
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
    suspend fun createLinkToken(context: YabaGraphQLContext, itemId: UUID? = null): LinkTokenResult {
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
