package tech.alexib.yaba.server.feature.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import java.time.OffsetDateTime
import java.util.UUID

interface UserRepository {
    suspend fun createUser(userEntity: UserEntity): Either<DbException, UserEntity>
    suspend fun deleteUser(id: UserId): Either<DbException, Unit>
    suspend fun findById(id: UserId): Either<DbException, UserEntity>
    suspend fun findByEmail(email: String): Either<DbException, UserEntity>
    suspend fun findByIds(ids: List<UUID>): List<UserEntity>
    suspend fun isUserActive(id: UserId): Boolean
    suspend fun setLastLogin(id: UserId)

}

@Repository
class UserRepositoryImpl(
    private val connectionFactory: ConnectionFactory,
    private val r2dbcConverter: R2dbcConverter
) : UserRepository {

    private val dbClient: DatabaseClient by lazy { DatabaseClient.create(connectionFactory) }

    private val template: R2dbcEntityTemplate by lazy { R2dbcEntityTemplate(connectionFactory) }

    override suspend fun createUser(userEntity: UserEntity): Either<DbException, UserEntity> {
        return try {
            template.insert(userEntity).awaitFirst().right()
        } catch (e: Exception) {
            DbException.SqlException(e.localizedMessage).left()
        }
    }

    suspend fun createUser(user: User): User? {
        return template.insert(user.toEntity()).awaitFirstOrNull()?.toDomain()
    }

    override suspend fun deleteUser(id: UserId): Either<DbException, Unit> {
        return try {
            dbClient.sql("delete from users_table where id = :id").bind("id", id.value).await()
            Unit.right()
        } catch (e: Exception) {
            DbException.SqlException(e.localizedMessage).left()
        }
    }

    override suspend fun findById(id: UserId): Either<DbException, UserEntity> {
        return try {
            template.selectOne(Query.query(where("id").`is`(id.value)), UserEntity::class.java)
                .awaitFirstOrNull()?.right() ?: DbException.NotFound("User not found for id ${id.value}").left()
        } catch (e: Exception) {
            DbException.SqlException(e.localizedMessage).left()
        }
    }

    override suspend fun findByEmail(email: String): Either<DbException, UserEntity> =
        try {
            template.selectOne(
                Query.query(where("email").`is`(email)),
                UserEntity::class.java
            ).awaitFirstOrNull()?.right() ?: DbException.NotFound("User not found - username: $email").left()

        } catch (e: Exception) {
            DbException.SqlException(e.localizedMessage).left()
        }

    fun findAll(): Flow<UserEntity> = template.select<UserEntity>().all().asFlow()

    override suspend fun findByIds(ids: List<UUID>): List<UserEntity> {
        return dbClient.sql(
            """
          select * from users_table where id in :ids
      """.trimIndent()
        ).bind("ids", ids).map { row -> r2dbcConverter.read(UserEntity::class.java, row) }.flow().toList()
    }

    override suspend fun isUserActive(id: UserId): Boolean {
        val userResult = dbClient.sql(
            """
           select id, active from users_table where active is true and id = :id
       """.trimIndent()
        ).bind("id", id.value).fetch().first().awaitFirstOrNull()

        return userResult != null
    }

    override suspend fun setLastLogin(id: UserId) {
        dbClient.sql(
            """
           insert into last_login_table (user_id, last_login)
            values (:id,:time)
            on conflict (user_id) do update set last_login = :time
       """.trimIndent()
        ).bind("id", id.value).bind("time", OffsetDateTime.now()).await()
    }
}

sealed class DbException {
    abstract val message: String

    data class Duplicate(override val message: String) : DbException()
    data class NotFound(override val message: String) : DbException()
    data class SqlException(override val message: String) : DbException()
}
