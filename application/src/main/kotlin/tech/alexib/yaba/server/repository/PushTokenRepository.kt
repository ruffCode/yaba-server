package tech.alexib.yaba.server.repository

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import tech.alexib.yaba.domain.user.UserId


interface PushTokenRepository {
    suspend fun insertToken(userId: UserId, token: String)
    fun getUserTokens(userId: UserId): Flow<String>
    suspend fun deleteToken(token: String)
}


@Repository
class PushTokenRepositoryImpl(
    private val connectionFactory: ConnectionFactory
) : PushTokenRepository {
    private val dbClient by lazy { DatabaseClient.create(connectionFactory) }

    override suspend fun insertToken(userId: UserId, token: String) {
        dbClient.sql(
            """
           insert into user_push_tokens (token, user_id) values (:token,:userId)
           on conflict (token) do nothing 
       """.trimIndent()
        ).bind("token", token)
            .bind("userId", userId.value).await()
    }

    override fun getUserTokens(userId: UserId): Flow<String> {
        return dbClient.sql(
            """
    select token from user_push_tokens where user_id = :userId
""".trimIndent()
        ).bind("userId", userId.value).map { row -> row["token"] as String }.flow()
    }

    override suspend fun deleteToken(token: String) {
        dbClient.sql(
            """
            delete from user_push_tokens where token = :token
        """.trimIndent()
        ).bind("token", token).await()
    }
}
