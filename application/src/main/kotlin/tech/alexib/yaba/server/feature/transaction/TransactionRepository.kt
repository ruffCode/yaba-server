package tech.alexib.yaba.server.feature.transaction

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.account.AccountId
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
}

private val logger = KotlinLogging.logger { }

@Repository
class TransactionRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val r2dbcConverter: R2dbcConverter
) : TransactionRepository {

    private val client: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }
    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }


    override fun create(transactions: List<TransactionTableEntity>): Flow<TransactionTableEntity> {
        return transactions.asFlow().map { create(it) }
    }

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
        return client.sql(
            """
            select * from transactions where id in (:ids)
        """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow().toList()
    }

    override suspend fun findByItemId(ids: List<UUID>): List<TransactionEntity> {
        return client.sql(
            """
            select * from transactions where transactions.item_id in (:ids)
        """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow().toList()
    }

    override suspend fun findByAccountId(ids: List<UUID>): List<TransactionEntity> {
        return client.sql(
            """
            select * from transactions where transactions.account_id in (:ids)
        """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(TransactionEntity::class.java, row) }.flow().toList()
    }

    override suspend fun deleteTransactions(itemId: ItemId) {
        client.sql(
            """
            delete from transactions where item_id = :itemId
        """.trimIndent()
        ).bind("itemId", itemId.value).await()
    }
}




