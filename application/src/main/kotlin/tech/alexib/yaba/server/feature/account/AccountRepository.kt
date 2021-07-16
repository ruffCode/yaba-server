package tech.alexib.yaba.server.feature.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.bind
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.item.ItemRepository
import java.util.UUID

interface AccountRepository {
    suspend fun create(entity: AccountEntity): AccountEntity
    suspend fun createAccounts(plaidItemId: String, accounts: List<AccountInsertRequest>): Flow<AccountEntity>
    suspend fun findByPlaidAccountId(plaidAccountId: String): Either<Unit, AccountEntity>
    suspend fun findByItemId(itemId: ItemId): Flow<AccountEntity>
    suspend fun findByUserId(userId: UserId): Flow<AccountEntity>
    suspend fun findByIds(ids: List<UUID>): List<AccountEntity>
    suspend fun setHidden(plaidAccountId: String, hide: Boolean)
    suspend fun deleteByItemId(itemId: ItemId)
    suspend fun setHidden(id: UUID, hide: Boolean)
    suspend fun findById(id: UUID): AccountEntity
}

private val logger = KotlinLogging.logger { }

@Repository
class AccountRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val itemRepository: ItemRepository,
    private val tx: Tx,
    private val r2dbcConverter: R2dbcConverter
) : AccountRepository {

    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }
    private val client: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }


    override suspend fun createAccounts(
        plaidItemId: String,
        accounts: List<AccountInsertRequest>
    ): Flow<AccountEntity> {

        val ids = itemRepository.findAll().toList().map { it.id }
        logger.info { "IDS $ids" }
        val itemId = itemRepository.findByPlaidId(plaidItemId).fold({ throw Exception("not found") }, {
            it.id
        })

        return tx.invoke {
            accounts.map { it.toEntity(itemId) }.asFlow().map { create(it) }
        }


    }

    override suspend fun create(entity: AccountEntity): AccountEntity {
        return try {

            client.sql(
                """
            insert into accounts_table (item_id, plaid_account_id, name, mask, official_name, current_balance,
             available_balance, iso_currency_code, unofficial_currency_code, type, subtype,credit_limit)
             values (:itemId,:accountId,:name,:mask,:officialName,:current,:available,:iso,:unofficial,:type,:subtype,:creditLimit)
             on conflict (plaid_account_id)
             do update set 
             current_balance = :current,
             available_balance = :available,
             credit_limit = :creditLimit
             returning *
        """.trimIndent()
            )
                .bind("itemId", entity.itemId)
                .bind("accountId", entity.plaidAccountId)
                .bind("name", entity.name)
                .bind("mask", entity.mask)
                .bind("officialName", entity.officialName)
                .bind("current", entity.currentBalance)
                .bind("available", entity.availableBalance)
                .bind("iso", entity.isoCurrencyCode)
                .bind("unofficial", entity.unofficialCurrencyCode)
                .bind("type", entity.type)
                .bind("creditLimit", entity.creditLimit)
                .bind("subtype", entity.subtype).map { row, _ -> AccountEntity.fromRow(row) }.first().awaitFirst()
        } catch (e: Exception) {
            when (e) {
                is BadSqlGrammarException -> {
                    logger.error { e.r2dbcException }
                    logger.error { e.sql }
                    throw e
                }
                else -> throw e
            }
        }


    }

    override suspend fun findByPlaidAccountId(plaidAccountId: String): Either<Unit, AccountEntity> {
        return template.selectOne(
            query(where("plaid_account_id").`is`(plaidAccountId)), AccountEntity::class.java
        )
            .awaitFirstOrNull()?.right() ?: Unit.left()
    }

    override suspend fun findByItemId(itemId: ItemId): Flow<AccountEntity> {
        return client.sql(
            """
            select *  from accounts where item_id = :itemId 
        """.trimIndent()
        ).bind("itemId", itemId.value).map { row -> r2dbcConverter.read(AccountEntity::class.java, row) }
            .all().asFlow()
    }

    override suspend fun findByUserId(userId: UserId): Flow<AccountEntity> {

        return client.sql(
            """
            select *  from accounts where user_id = :userId 
        """.trimIndent()
        ).bind("userId", userId.value).map { row -> r2dbcConverter.read(AccountEntity::class.java, row) }
            .all().asFlow()

    }

    override suspend fun findByIds(ids: List<UUID>): List<AccountEntity> {
        return client.sql(
            """
           select * from accounts where id in (:ids)
       """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(AccountEntity::class.java, row) }.flow().toList()
    }

    override suspend fun setHidden(plaidAccountId: String, hide: Boolean) {
        client.sql(
            """
          update accounts_table set hidden = :hide where plaid_account_id = :id
      """.trimIndent()
        ).bind("hide", hide).bind("id", plaidAccountId).await()
    }

    override suspend fun setHidden(id: UUID, hide: Boolean) {
        client.sql(
            """
          update accounts_table set hidden = :hide where id = :id
      """.trimIndent()
        ).bind("hide", hide).bind("id", id).await()
    }

    override suspend fun deleteByItemId(itemId: ItemId) {
        client.sql(
            """
            delete from accounts_table where item_id = :itemId
        """.trimIndent()
        ).bind("itemId", itemId.value).await()
    }

    override suspend fun findById(id: UUID): AccountEntity {
        return client.sql(
            """
              select * from accounts where id = :id
          """.trimIndent()
        ).bind("id", id).map { row -> r2dbcConverter.read(AccountEntity::class.java, row) }.one().awaitFirst()
    }
}

interface Tx {
    suspend operator fun <R> invoke(
        rollbackOnly: Boolean = false,
        block: suspend () -> R,
    ): R
}

@Service
internal class TxImpl(
    val operator: TransactionalOperator,
) : Tx {
    override suspend fun <R> invoke(rollbackOnly: Boolean, block: suspend () -> R): R {
        val nullMarker = Any()
        val nullMono = mono { nullMarker }
        val resultMono = mono<Any?> { block() }

        val flux = operator.execute { tx ->
            if (rollbackOnly) tx.setRollbackOnly()
            resultMono.switchIfEmpty(nullMono)
        }

        val result = flux.awaitLast()

        @Suppress("UNCHECKED_CAST")
        return result.takeIf { it != nullMarker } as R
    }
}
