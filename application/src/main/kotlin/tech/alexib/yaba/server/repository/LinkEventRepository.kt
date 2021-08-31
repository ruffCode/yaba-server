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
package tech.alexib.yaba.server.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import tech.alexib.yaba.server.entity.LinkEventEntity

interface LinkEventRepository {
    suspend fun create(entity: LinkEventEntity): Either<LinkEventRepositoryError.Duplicate, LinkEventEntity>
}

@Repository
class LinkEventRepositoryImpl(private val connectionFactory: ConnectionFactory) : LinkEventRepository {
    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }

    override suspend fun create(entity: LinkEventEntity): Either<LinkEventRepositoryError.Duplicate, LinkEventEntity> {
        return try {
            template.insert(entity).awaitFirst().right()
        } catch (e: Exception) {
            when (e) {
                is DataIntegrityViolationException -> LinkEventRepositoryError.Duplicate.left()
                else -> throw e
            }
        }
    }
}

sealed class LinkEventRepositoryError {
    object Duplicate : LinkEventRepositoryError()
    object NotFound : LinkEventRepositoryError()
}
