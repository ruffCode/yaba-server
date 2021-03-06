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
package tech.alexib.yaba.server.feature.transaction

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.feature.account.accountId
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.service.TransactionService
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class TransactionQuery(
    private val transactionRepository: TransactionRepository,
    private val transactionService: TransactionService,
) : Query {

    @Authenticated
    @GraphQLDescription("Returns all of user's transactions")
    suspend fun transactionsByUser(context: YabaGraphQLContext): List<TransactionDto> {
        return transactionRepository.findByUserId(context.id()).map { it.toDto() }.toList()
    }

    @Authenticated
    @GraphQLDescription("Returns all transactions for accountId")
    suspend fun transactionByAccountId(accountId: UUID): List<TransactionDto> =
        transactionRepository.findByAccountId(accountId.accountId()).map {
            it.toDto()
        }.toList()

    @Authenticated
    suspend fun transactionsByItemId(itemId: UUID): List<TransactionDto> =
        transactionRepository.findByItemId(itemId.itemId()).map { it.toDto() }.toList()

    @Authenticated
    suspend fun transactionsByIds(ids: List<UUID>): List<TransactionDto> =
        transactionRepository.findById(ids).map { it.toDto() }

    @Authenticated
    suspend fun transactionsUpdated(context: YabaGraphQLContext, updateId: UUID): TransactionsUpdatedDto? {
        val userId = context.id()
        return transactionService.getTransactionUpdate(userId, updateId)
    }
}
