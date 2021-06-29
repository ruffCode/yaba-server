package tech.alexib.yaba.server.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import tech.alexib.yaba.server.AccountStub
import tech.alexib.yaba.server.ItemStub
import tech.alexib.yaba.server.UsersStub
import tech.alexib.yaba.server.config.BaseIntegrationTest

import tech.alexib.yaba.server.feature.account.AccountEntity
import tech.alexib.yaba.server.feature.account.AccountInsertRequest
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.item.toEntity
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.toEntity

private val logger = KotlinLogging.logger { }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AccountTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var itemRepository: ItemRepository

    val accounts = AccountStub.plaidAccounts
    suspend fun initDb() {
        userRepository.createUser(UsersStub.user.toEntity())
        itemRepository.createItem(ItemStub.item.toEntity())
    }

    @BeforeAll
    fun setup() {
        runBlocking { initDb() }
    }

    @Test
    fun insertAccounts() {

        runBlocking {
            try {
                createAccounts().toList()
                createAccounts().toList()
            } catch (e: Throwable) {
                logger.error { e }
                fail(e)
            }
        }
    }

    private suspend fun createAccounts(): Flow<AccountEntity> {
        return accountRepository.createAccounts(
            ItemStub.item.plaidItemId,
            accounts = accounts.accounts.map { AccountInsertRequest(it) })
    }
}
