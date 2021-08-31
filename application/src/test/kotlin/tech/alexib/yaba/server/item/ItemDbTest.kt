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
package tech.alexib.yaba.server.item

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.PlaidAccessToken
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.assertIsError
import tech.alexib.yaba.server.assertIsOk
import tech.alexib.yaba.server.config.BaseIntegrationTest
import tech.alexib.yaba.server.feature.item.ItemEntity
import tech.alexib.yaba.server.feature.item.ItemException
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.toEntity
import tech.alexib.yaba.server.user.usersStub

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ItemDbTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var itemRepository: ItemRepository

    @Autowired
    lateinit var connectionFactory: ConnectionFactory

    @BeforeAll
    fun setup() {
        initDb()
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
        }
    }

    @Test
    fun `does not insert duplicate`() {
        runBlocking {
            assertIsError(itemRepository.createItem(itemStub), ItemException.Duplicate)
        }
    }

    @Test
    fun `find item by id`() {
        runBlocking {
            assertIsOk(itemRepository.findById(ItemId(itemStub.id)))
        }
    }

    @Test
    fun `find items by userId`() {
        runBlocking {
            itemRepository.findByUserId(usersStub.first().id).toList().let {
                assert(it.size == 1)
            }
        }
    }

    @Test
    fun `find item by accessToken`() {
        runBlocking {
            assertIsOk(itemRepository.findByAccessToken(itemStub.accessToken))
        }
    }

    @Test
    fun `find item by institution id`() {
        runBlocking {
            assertIsOk(itemRepository.findByInstitutionId(itemStub.institutionId, usersStub.first().id))
        }
    }

    @Test
    fun `find item by plaid id`() {
        runBlocking {
            assertIsOk(itemRepository.findByPlaidId(itemStub.plaidItemId))
        }
    }

    @Test
    fun `update item status`() {
        assertDoesNotThrow {
            runBlocking {
                itemRepository.updateStatus(itemStub.id, "bad")
            }
        }
    }

    @Test
    fun `unlinked count is incremented`() {
        runBlocking {
            itemRepository.unlink(itemStub.id.itemId(), usersStub.first().id)

            val updated2 = itemRepository.relink(
                InstitutionId(itemStub.institutionId),
                usersStub.first().id,
                PlaidAccessToken("access")
            )
            Assertions.assertEquals(1, updated2.timesUnlinked)
            Assertions.assertTrue(updated2.linked)
            itemRepository.unlink(itemStub.id.itemId(), usersStub.first().id)
        }
    }

    private fun initDb() {
        runBlocking {
            usersStub.forEach {
                userRepository.createUser(it.toEntity())
            }
            itemRepository.createItem(itemStub)
        }
    }
}

private val itemStub = ItemEntity(
    userId = usersStub.first().id.value,
    plaidItemId = "id1",
    accessToken = "access",
    institutionId = "1234",
    status = "good"
)
