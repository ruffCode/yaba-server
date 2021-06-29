package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.fcm.FCMService
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.repository.PushTokenRepository

@Component
class UserPushTokenMutation(
    private val pushTokenRepository: PushTokenRepository,
    private val fcmService: FCMService
) : Mutation {

    @Authenticated
    suspend fun pushTokenInsert(context: YabaGraphQLContext, token: String): Boolean {
        val userId = context.id()
        pushTokenRepository.insertToken(userId, token)
        return true
    }
    @Authenticated
    suspend fun pushTokenDelete(token: String): Boolean {
        pushTokenRepository.deleteToken(token)
        return true
    }
}
