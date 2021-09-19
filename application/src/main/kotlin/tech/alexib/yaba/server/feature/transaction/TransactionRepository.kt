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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.account.AccountId
import tech.alexib.yaba.server.util.bind
import java.time.LocalDate
import java.util.UUID

interface TransactionRepository {
    fun create(transactions: List<TransactionTableEntity>): Flow<TransactionTableEntity>
    suspend fun create(transaction: TransactionTableEntity): TransactionTableEntity
    fun findByAccountId(accountId: AccountId): Flow<TransactionEntity>
    suspend fun findById(ids: List<UUID>): List<TransactionEntity>
    fun findByItemId(itemId: ItemId): Flow<TransactionEntity>
    fun findByUserId(userId: UserId): Flow<TransactionEntity>
    suspend fun findByItemId(ids: List<UUID>): List<TransactionEntity>
    suspend fun findByAccountId(ids: List<UUID>): List<TransactionEntity>
    fun findInRange(plaidItemId: String, startDate: LocalDate, endDate: LocalDate): Flow<TransactionEntity>
    suspend fun deleteTransactions(plaidIds: List<String>)
    suspend fun deleteTransactions(itemId: ItemId)
    fun findByPlaidIds(plaidIds: List<String>): Flow<UUID>
}

private val logger = KotlinLogging.logger { }

@Repository
class TransactionRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val r2dbcConverter: R2dbcConverter,
) : TransactionRepository {

    private val client: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }
    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }

    override fun create(transactions: List<TransactionTableEntity>): Flow<TransactionTableEntity> =
        client.inConnectionMany { connection ->
            val statement = connection.createStatement(
                """
                insert into transactions_table (account_id, plaid_transaction_id, plaid_category_id, category,
            subcategory, type,  amount, iso_currency_code, unofficial_currency_code, date, pending, account_owner,
            merchant_name, category_id,name) values ($1, $2, $3 ,$4, $5, $6, $7, $8, $9 ,$10, $11, $12, $13, $14,$15)
             returning *
                """.trimIndent()
            )

            transactions.forEach { transaction ->
                statement.apply {
                    with(transaction) {
                        bind(0, accountId)
                        bind(1, plaidTransactionId)
                        bind(2, plaidCategoryId)
                        bind(3, category)
                        bind(4, subcategory)
                        bind(5, type)
                        bind(6, amount)
                        bind(7, isoCurrencyCode)
                        bind(8, unofficialCurrencyCode)
                        bind(9, date)
                        bind(10, pending)
                        bind(11, accountOwner)
                        bind(12, merchantName)
                        bind(13, plaidCategoryId?.toIntOrNull(), Int::class.java)
                        bind(14, name)
                    }.add()
                }
            }
            Flux.from(statement.execute()).flatMap {
                it.map { row, _ ->
                    r2dbcConverter.read(
                        TransactionTableEntity::class.java,
                        row
                    )
                }
            }
        }.asFlow()

    override suspend fun create(transaction: TransactionTableEntity): TransactionTableEntity {

        return try {
            template.insert<TransactionTableEntity>().into("transactions_table").usingAndAwait(transaction)
        } catch (e: Throwable) {
            when (e) {
                is DuplicateKeyException -> {
                    logger.error { e }
                    transaction
                }
                else -> throw e
            }
        }
    }

    override fun findByAccountId(accountId: AccountId): Flow<TransactionEntity> {
        return client.sql(
            """
          SELECT * FROM transactions WHERE account_id = :id ORDER BY date DESC
            """.trimIndent()
        ).bind("id", accountId.value).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow()
    }

    override fun findByItemId(itemId: ItemId): Flow<TransactionEntity> {
        return client.sql(
            """
    SELECT * FROM transactions WHERE item_id = :id ORDER BY date DESC
            """.trimIndent()
        ).bind("id", itemId.value).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow()
    }

    override fun findByUserId(userId: UserId): Flow<TransactionEntity> {
        return client.sql(
            """
            SELECT * FROM transactions WHERE user_id = :id ORDER BY date DESC
            """.trimIndent()
        ).bind("id", userId.value).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow()
    }

    override fun findInRange(plaidItemId: String, startDate: LocalDate, endDate: LocalDate): Flow<TransactionEntity> {
        return client.sql(
            """
    SELECT
        *
      FROM
        transactions
      WHERE
        plaid_item_id = :id
        AND date >= :start
        AND date <= :end
      ORDER BY
        date DESC
            """.trimIndent()
        ).bind("id", plaidItemId)
            .bind("start", startDate)
            .bind("end", endDate).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow()
    }

    override suspend fun deleteTransactions(plaidIds: List<String>) {
        plaidIds.forEach {
            client.sql(
                """
                delete from transactions_table where plaid_transaction_id = :id
                """.trimIndent()
            ).bind("id", it).await()
        }
    }

    override suspend fun findById(ids: List<UUID>): List<TransactionEntity> {
        return if (ids.isNotEmpty()) client.sql(
            """
            select * from transactions where id in (:ids)
            """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow().toList()
        else emptyList()
    }

    override suspend fun findByItemId(ids: List<UUID>): List<TransactionEntity> {
        return if (ids.isNotEmpty()) client.sql(
            """
            select * from transactions where transactions.item_id in (:ids)
            """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow()
            .toList()
        else emptyList()
    }

    override suspend fun findByAccountId(ids: List<UUID>): List<TransactionEntity> {
        return if (ids.isNotEmpty()) {
            client.sql(
                """
            select * from transactions where transactions.account_id in (:ids)
                """.trimIndent()
            ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }
                .flow().toList()
        } else emptyList()
    }

    override suspend fun deleteTransactions(itemId: ItemId) {
        val itemTransactionIds = client.sql(
            """
            select id from transactions where item_id = :itemId
            """.trimIndent()
        ).bind("itemId", itemId.value).map { row -> row["id"] as UUID }.flow().toList()
        logger.error { "transactionIds $itemTransactionIds" }
        client.sql(
            """
            delete from transactions_table where id in (:ids)
            """.trimIndent()
        ).bind("ids", itemTransactionIds).await()
    }

    override fun findByPlaidIds(plaidIds: List<String>): Flow<UUID> {
        return if (plaidIds.isNotEmpty()) {
            client.sql(
                """
           select id from transactions where transactions.plaid_transaction_id in (:ids)
                """.trimIndent()
            ).bind("ids", plaidIds).map { row -> row["id"] as UUID }.flow()
        } else emptyFlow()
    }
}
