package tech.alexib.yaba.server.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import tech.alexib.yaba.server.entity.LinkEventEntity


interface LinkEventRepository {
    suspend fun create(entity: LinkEventEntity): Either<LinkEventRepositoryError.Duplicate, LinkEventEntity>
}

@Repository
class LinkEventRepositoryImpl(private val connectionFactory: ConnectionFactory) : LinkEventRepository {
    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }

    override suspend fun create(entity: LinkEventEntity): Either<LinkEventRepositoryError.Duplicate, LinkEventEntity> {
        return try {
            template.insert(entity).awaitFirst().right()
        } catch (e: Exception) {
            when (e) {
                is DataIntegrityViolationException -> LinkEventRepositoryError.Duplicate.left()
                else -> throw e
            }
        }
    }

}

sealed class LinkEventRepositoryError {
    object Duplicate : LinkEventRepositoryError()
    object NotFound : LinkEventRepositoryError()
}
