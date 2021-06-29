package tech.alexib.yaba.server.feature.item

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
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
    private val itemRepository: ItemRepository
) : CoroutineDataLoader<UUID, List<ItemDto>>() {
    override suspend fun batchLoad(keys: List<UUID>): List<List<ItemDto>> {
        return keys.map { uuid -> itemRepository.findByUserId(UserId(uuid)).map { it.toDto() }.toList() }
    }
}

private val logger = KotlinLogging.logger {}

@Component
class ItemsByUserIdDataFetcher : DataFetcher<CompletableFuture<List<ItemDto>>> {

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<ItemDto>> {
        val userId: UUID = environment.getSource<UserDto>().id
        return environment.getValueFromDataLoader(ItemsByUserIdDataLoader::class, userId)
    }
}

