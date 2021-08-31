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
            accounts = accounts.accounts.map { AccountInsertRequest(it) }
        )
    }
}
