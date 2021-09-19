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
package tech.alexib.yaba.server.graphql

import com.expediagroup.graphql.server.operations.Subscription
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asPublisher
import mu.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.feature.transaction.TransactionDto
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class TestSubscription(
    private val transactionRepository: TransactionRepository,
) : Subscription {

    fun testSub(user: String): Publisher<Int> {
        logger.debug { user }
        return (1..550).asFlow().map {
            delay(2000)
            it
        }.asPublisher()
    }

    fun testUserTransactions(userId: UUID): Publisher<TransactionDto> {
        return transactionRepository.findByUserId(userId.userId()).map { it.toDto() }.asPublisher()
    }
}
