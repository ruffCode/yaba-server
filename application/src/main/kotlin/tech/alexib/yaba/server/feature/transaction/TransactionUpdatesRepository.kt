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

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.bind
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import tech.alexib.yaba.domain.user.UserId
import java.util.UUID

interface TransactionUpdatesRepository {
    suspend fun insert(entity: TransactionUpdatesEntity)
    fun getById(id: UUID, userId: UserId): Flow<TransactionUpdatesEntity>
}

@Repository
class TransactionUpdatesRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val r2dbcConverter: R2dbcConverter
) : TransactionUpdatesRepository {

    private val client: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }

    override suspend fun insert(entity: TransactionUpdatesEntity) {
        client.sql(
            """
          insert into transaction_updates (id,user_id,added,removed)
          values (:id,:userId,:added,:removed)
            """.trimIndent()
        )
            .bind("id", entity.id)
            .bind("userId", entity.userId)
            .bind("added", entity.added?.toTypedArray())
            .bind("removed", entity.removed?.toTypedArray())
            .await()
    }

    override fun getById(id: UUID, userId: UserId): Flow<TransactionUpdatesEntity> =
        client.sql(
            """
            select * from transaction_updates where id = :id and user_id = :userId
            """.trimIndent()
        ).bind("id", id).bind("userId", userId.value)
            .map { row -> r2dbcConverter.read(TransactionUpdatesEntity::class.java, row) }.flow()
}
