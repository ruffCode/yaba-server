package tech.alexib.yaba.server.transaction

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import tech.alexib.yaba.domain.item.PlaidItemId
import tech.alexib.yaba.server.AccountStub
import tech.alexib.yaba.server.ItemStub
import tech.alexib.yaba.server.TransactionsStub
import tech.alexib.yaba.server.UsersStub
import tech.alexib.yaba.server.config.BaseIntegrationTest
import tech.alexib.yaba.server.feature.account.AccountInsertRequest
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.account.accountId
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.item.toEntity
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import tech.alexib.yaba.server.feature.transaction.TransactionTableEntity
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.toEntity
import tech.alexib.yaba.server.service.TransactionService
import java.util.UUID

private val logger = KotlinLogging.logger { }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TransactionDbTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    lateinit var connectionFactory: ConnectionFactory

    val accounts = AccountStub.plaidAccounts
    val transactions = TransactionsStub.transactions.transactions
    suspend fun initDb() {
        userRepository.createUser(UsersStub.user.toEntity())
        itemRepository.createItem(ItemStub.item.toEntity())
        accountRepository.createAccounts(
            ItemStub.item.plaidItemId,
            accounts = accounts.accounts.map { AccountInsertRequest(it) }).toList()
    }

    val client: DatabaseClient by lazy {
        DatabaseClient.create(connectionFactory)
    }

    @BeforeAll
    fun setup() {
        runBlocking { initDb() }
    }

    @AfterAll
    fun breakDown() {

        runBlocking {
            client.sql(
                """
            delete from users_table where id is not null 
        """.trimIndent()
            ).await()

            client.sql(
                """
                delete from items_table where id is not null 
            """.trimIndent()
            ).await()

            client.sql(
                """
                delete from accounts_table where id is not null
            """.trimIndent()
            ).await()
        }


    }

    @Order(0)
    @Test
    fun insertTransactions() {

        runBlocking {
            val toSave = transactions.map { transaction ->
                val accountId =
                    accountRepository.findByPlaidAccountId(transaction.accountId).findOrNull { it.id != null }?.id
                        ?: throw Exception("accountid not found")
                TransactionTableEntity.fromPlaid(transaction, accountId.accountId())
            }
            try {
                val saved = transactionRepository.create(toSave).toList()
                assert(saved.size == transactions.size)
            } catch (e: Throwable) {
                logger.error { e }
                throw e
            }
        }
    }

    @Order(1)
    @Test
    fun findsByPlaidTransactionIds() {

        runBlocking {
            kotlin.runCatching {
                val ids = transactions.map { it.transactionId }

                val plaidIds = transactionRepository.findByPlaidIds(ids).toList()
                assert(plaidIds.size == ids.size)

            }.getOrElse {
                when (it) {
                    is BadSqlGrammarException -> {
                        logger.error { "bad grammar" }
                        logger.error { it.sql }
                        logger.error { it.r2dbcException }
                        throw  it
                    }

                }
                logger.error { it }
                throw it
            }

        }
    }

    @Order(1)
    @Test
    fun findsByPlaidTransactionIdsWithEmptyList() {

        runBlocking {
            kotlin.runCatching {


                val plaidIds = transactionRepository.findByPlaidIds(emptyList()).toList()
                assert(plaidIds.isEmpty())

            }.getOrElse {
                when (it) {
                    is BadSqlGrammarException -> {
                        logger.error { "bad grammar" }
                        logger.error { it.sql }
                        logger.error { it.r2dbcException }
                        throw  it
                    }

                }
                logger.error { it }
                throw it
            }

        }
    }

    @Order(2)
    @Test
    fun insertsTransactionUpdate() {
        runBlocking {

            val transactions = transactionRepository.findByItemId(ItemStub.item.id).toList()
            val added = transactions.take(10).map { it.toDto().id }
            val removed = transactions.takeLast(10).map { it.toDto().id }
            transactionService.insertTransactionUpdate(PlaidItemId(ItemStub.item.plaidItemId), added, removed)

            val updateIds = client.sql(
                """
                select * from transaction_updates
            """.trimIndent()
            ).map { row -> row["id"] as UUID }.flow().toList()

            assert(updateIds.isNotEmpty())
            assert(updateIds.size == 1)

            val update = transactionService.getTransactionUpdate(UsersStub.user.id, updateIds.first())

            Assertions.assertNotNull(update?.added)
            Assertions.assertNotNull(update?.removed)
            Assertions.assertEquals(10,update?.added?.size)
            Assertions.assertEquals(10,update?.removed?.size)

        }
    }
}
