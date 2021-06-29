package tech.alexib.yaba.server.feature.account

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.UserId
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
class AccountsByUserIdDataFetcher : DataFetcher<CompletableFuture<List<AccountDto>>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<List<AccountDto>> {
        val userId = environment.getSource<UserDto>().id
        return environment.getValueFromDataLoader(AccountsByUserIdDataLoader::class, userId)
    }
}

