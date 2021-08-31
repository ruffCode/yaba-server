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

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.account.AccountId
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.graphql.util.CoroutineDataLoader
import tech.alexib.yaba.server.graphql.util.getValueFromDataLoader
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Component
class TransactionDataLoader(private val repository: TransactionRepository) :
    CoroutineDataLoader<UUID, TransactionDto>() {

    override suspend fun batchLoad(keys: List<UUID>): List<TransactionDto> {
        return repository.findById(keys).map { it.toDto() }
    }
}

@Component
class TransactionsByAccountIdDataLoader(private val repository: TransactionRepository) :
    CoroutineDataLoader<UUID, List<TransactionDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<TransactionDto>> {
        return keys.map { uuid -> repository.findByAccountId(AccountId(uuid)).toList().map { it.toDto() } }
    }
}

@Component
class TransactionsByItemIdDataLoader(private val repository: TransactionRepository) :
    CoroutineDataLoader<UUID, List<TransactionDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<TransactionDto>> {
        return keys.map { uuid -> repository.findByItemId(ItemId(uuid)).toList().map { it.toDto() } }
    }
}

@Component
class TransactionsByUserIdDataLoader(private val repository: TransactionRepository) :
    CoroutineDataLoader<UUID, List<TransactionDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<TransactionDto>> {
        return keys.map { uuid -> repository.findByUserId(UserId(uuid)).map { it.toDto() }.toList() }
    }
}

@Component
class TransactionsByUserIdDataFetcher : DataFetcher<CompletableFuture<List<TransactionDto>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<TransactionDto>> {
        val userId = environment.getSource<UserDto>().id
        return environment.getValueFromDataLoader(TransactionsByUserIdDataLoader::class, userId)
    }
}

@Component
class TransactionsByAccountIdDataFetcher : DataFetcher<CompletableFuture<List<TransactionDto>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<TransactionDto>> {
        val accountId = environment.getSource<AccountDto>().id
        val hidden = environment.getSource<AccountDto>().hidden
        return environment.getValueFromDataLoader(
            TransactionsByAccountIdDataLoader::class,
            if (!hidden) accountId else UUID.randomUUID()
        )
    }
}

@Component
class TransactionByItemIdDataFetcher : DataFetcher<CompletableFuture<List<TransactionDto>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<TransactionDto>> {
        val itemId = environment.getSource<ItemDto>().id
        return environment.getValueFromDataLoader(TransactionsByItemIdDataLoader::class, itemId)
    }
}
