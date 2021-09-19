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
package tech.alexib.yaba.server.feature.item

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.graphql.util.CoroutineDataLoader
import tech.alexib.yaba.server.graphql.util.getValueFromDataLoader
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Component
class ItemDataLoader(private val repository: ItemRepository) : CoroutineDataLoader<UUID, ItemDto>() {
    override suspend fun batchLoad(keys: List<UUID>): List<ItemDto> {
        return repository.findByIds(keys).map { it.toDto() }
    }
}

@Component
class ItemsByItemIdDataFetcher : DataFetcher<CompletableFuture<ItemDto>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<ItemDto> {
        val itemId = environment.getSource<AccountDto>().itemId
        return environment.getValueFromDataLoader(ItemDataLoader::class, itemId)
    }
}

@Component
class ItemsByUserIdDataLoader(
    private val itemRepository: ItemRepository,
) : CoroutineDataLoader<UUID, List<ItemDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<ItemDto>> {
        return keys.map { uuid ->
            itemRepository.findByUserId(UserId(uuid), includeUnlinked = false).map { it.toDto() }.toList()
        }
    }
}

@Component
class ItemsByUserIdDataFetcher : DataFetcher<CompletableFuture<List<ItemDto>>> {

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<ItemDto>> {
        val userId: UUID = environment.getSource<UserDto>().id
        return environment.getValueFromDataLoader(ItemsByUserIdDataLoader::class, userId)
    }
}
