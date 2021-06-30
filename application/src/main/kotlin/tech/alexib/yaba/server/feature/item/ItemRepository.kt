package tech.alexib.yaba.server.feature.item


import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.PlaidAccessToken
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.util.serverError
import java.util.UUID

private val logger = KotlinLogging.logger { }

interface ItemRepository {
    suspend fun createItem(item: ItemEntity): Either<ItemException.Duplicate, ItemEntity>
    suspend fun findById(id: ItemId): Either<ItemException.NotFound, ItemEntity>
    suspend fun findByAccessToken(accessToken: String): Either<ItemException.NotFound, ItemEntity>
    suspend fun findByInstitutionId(institutionId: String, userId: UserId): Either<ItemException.NotFound, ItemEntity>
    suspend fun findByPlaidId(id: String): Either<ItemException.NotFound, ItemEntity>
    fun findByUserId(userId: UserId): Flow<ItemEntity>
    suspend fun updateStatus(id: UUID, status: String): ItemEntity
    suspend fun delete(id: UUID)
    suspend fun findByIds(ids: List<UUID>): List<ItemEntity>
    fun findAll(): Flow<ItemEntity>
    suspend fun unlink(itemId: ItemId, userId: UserId)
    suspend fun relink(institutionId: InstitutionId, userId: UserId, plaidAccessToken: PlaidAccessToken): ItemEntity

}

@Repository
class ItemRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val r2dbcConverter: R2dbcConverter
) :
    ItemRepository {
    private val client: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }
    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }

    override suspend fun createItem(item: ItemEntity): Either<ItemException.Duplicate, ItemEntity> {
        return try {
            template.insert(item).awaitFirst().right()
        } catch (e: Exception) {
            when (e) {
                is DataIntegrityViolationException -> ItemException.Duplicate.left()
                else -> throw e
            }
        }
    }


    override suspend fun findById(id: ItemId): Either<ItemException.NotFound, ItemEntity> {
        return template.selectOne(query(where("id").`is`(id.value)), ItemEntity::class.java)
            .awaitFirstOrNull()?.right()
            ?: ItemException.NotFound.left()
    }

    override suspend fun findByAccessToken(accessToken: String): Either<ItemException.NotFound, ItemEntity> {
        return template.selectOne(query(where("plaid_access_token").`is`(accessToken)), ItemEntity::class.java)
            .awaitFirstOrNull()?.right()
            ?: ItemException.NotFound.left()
    }

    override suspend fun findByInstitutionId(
        institutionId: String,
        userId: UserId
    ): Either<ItemException.NotFound, ItemEntity> {
        return template.selectOne(
            query(
                where("plaid_institution_id").`is`(institutionId)
                    .and(
                        where("user_id").`is`(userId.value)
                    )
            ), ItemEntity::class.java
        )
            .awaitFirstOrNull()?.right()
            ?: ItemException.NotFound.left()
    }

    override suspend fun findByPlaidId(id: String): Either<ItemException.NotFound, ItemEntity> {
        return template.selectOne(query(where("plaid_item_id").`is`(id)), ItemEntity::class.java)
            .awaitFirstOrNull()?.right()
            ?: ItemException.NotFound.left()
    }

    override fun findByUserId(userId: UserId): Flow<ItemEntity> =
        template.select(query(where("user_id").`is`(userId.value)), ItemEntity::class.java).asFlow()


    override suspend fun updateStatus(id: UUID, status: String): ItemEntity {
        return client.sql(
            """
            update items set status = :status where id = :id returning *
        """.trimIndent()
        ).bind("status", status).bind("id", id).map(::mapEntity).first()
            .awaitSingle()
    }

    override suspend fun delete(id: UUID) {
        client.sql(
            """
            delete from items_table where id = :id
        """.trimIndent()
        ).bind("id", id).fetch().rowsUpdated().awaitFirstOrNull()
    }

    override suspend fun findByIds(ids: List<UUID>): List<ItemEntity> {
        return client.sql(
            """
          select * from items where id in (:ids)
      """.trimIndent()
        ).bind("ids", ids).map(::mapEntity).flow().toList()
    }

    override fun findAll(): Flow<ItemEntity> {
        return client.sql(
            """
           select * from items
       """.trimIndent()
        ).map(::mapEntity).flow()
    }

    override suspend fun unlink(itemId: ItemId, userId: UserId) {
        client.sql(
            """
           update items_table set linked = false where id = :itemId and user_id = :userId
       """.trimIndent()
        ).bind("itemId", itemId.value).bind("userId", userId.value).await()
    }

    override suspend fun relink(
        institutionId: InstitutionId,
        userId: UserId,
        plaidAccessToken: PlaidAccessToken
    ): ItemEntity {
        return client.sql(
            """
            update items set (linked,plaid_access_token) = (true,:accessToken) 
            where plaid_institution_id = :institutionId and user_id = :userId
            returning *
        """.trimIndent()
        ).bind("institutionId", institutionId.value).bind("userId", userId.value)
            .bind("accessToken", plaidAccessToken.value).map(::mapEntity)
            .flow().first()
    }

    private fun mapEntity(row: Row): ItemEntity = r2dbcConverter.read(ItemEntity::class.java, row)

}

sealed class ItemException {
    object NotFound : ItemException()
    object Duplicate : ItemException()
    data class Fatal(val exception: Exception) : ItemException()
}

private fun handleSqlException(e: Exception): ItemException {
    return when (e) {
        is DataIntegrityViolationException -> {
            logger.error { e.localizedMessage }
            ItemException.Duplicate
        }
        is BadSqlGrammarException -> {
            logger.error { e.r2dbcException }
            logger.error { e.sql }
            throw serverError("")
        }
        else -> throw e
    }
}

