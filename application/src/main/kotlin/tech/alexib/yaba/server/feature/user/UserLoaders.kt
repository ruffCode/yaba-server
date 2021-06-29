package tech.alexib.yaba.server.feature.user

import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.UserDto

import tech.alexib.yaba.server.graphql.util.CoroutineDataLoader
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class UserDataLoader(private val repository: UserRepository) : CoroutineDataLoader<UUID, UserDto>() {

    override suspend fun batchLoad(keys: List<UUID>): List<UserDto> {
        logger.info { "UserDataLoader called with $keys" }
        return repository.findByIds(keys).map {
            UserDto(
                email = it.email,
                id = it.id
            )
        }
    }
}
