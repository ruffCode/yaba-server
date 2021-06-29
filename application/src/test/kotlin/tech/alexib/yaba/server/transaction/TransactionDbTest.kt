package tech.alexib.yaba.server.transaction

import io.kotest.matchers.shouldBe
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import tech.alexib.yaba.server.AccountStub
import tech.alexib.yaba.server.ItemStub
import tech.alexib.yaba.server.TransactionsStub
import tech.alexib.yaba.server.UsersStub
import tech.alexib.yaba.server.config.BaseIntegrationTest
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.account.AccountInsertRequest
import tech.alexib.yaba.server.feature.transaction.TransactionTableEntity
import tech.alexib.yaba.server.feature.account.accountId
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.item.toEntity
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import tech.alexib.yaba.server.feature.user.toEntity

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

    @BeforeAll
    fun setup() {
        runBlocking { initDb() }
    }

    @AfterAll
    fun breakDown() {
        val client = DatabaseClient.create(connectionFactory)
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
                saved.size.shouldBe(transactions.size)
            } catch (e: Throwable) {
                logger.error { e }
            }
        }
    }
}
