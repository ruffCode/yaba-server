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
package tech.alexib.yaba.server.feature.account

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.graphql.util.CoroutineDataLoader
import tech.alexib.yaba.server.graphql.util.getValueFromDataLoader
import java.util.UUID
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger { }

@Component
class AccountDataLoader(private val repository: AccountRepository) : CoroutineDataLoader<UUID, AccountDto>() {
    companion object {
        const val name = "AccountDataLoader"
    }

    override suspend fun batchLoad(keys: List<UUID>): List<AccountDto> {
        logger.info { "$name called with $keys" }
        return repository.findByIds(keys).map { it.toDto() }
    }
}

@Component
class AccountsByUserIdDataLoader(
    private val accountRepository: AccountRepository
) : CoroutineDataLoader<UUID, List<AccountDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<AccountDto>> {
        logger.info { "AccountsByUserIdDataLoader called with $keys" }
        return keys.map { uuid -> accountRepository.findByUserId(UserId(uuid)).map { it.toDto() }.toList() }
    }
}

@Component
class AccountsByItemIdDataLoader(
    private val accountRepository: AccountRepository
) : CoroutineDataLoader<UUID, List<AccountDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<AccountDto>> {
        return keys.map { uuid -> accountRepository.findByItemId(ItemId(uuid)).map { it.toDto() }.toList() }
    }
}

@Component
class AccountsByUserIdDataFetcher : DataFetcher<CompletableFuture<List<AccountDto>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<AccountDto>> {
        val userId = environment.getSource<UserDto>().id
        return environment.getValueFromDataLoader(AccountsByUserIdDataLoader::class, userId)
    }
}

@Component
class AccountsByItemIdDataFetcher : DataFetcher<CompletableFuture<List<AccountDto>>> {

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<AccountDto>> {
        val itemId = environment.getSource<ItemDto>().id
        return environment.getValueFromDataLoader(AccountsByItemIdDataLoader::class, itemId)
    }
}
