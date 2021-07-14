package tech.alexib.yaba.server.graphql.context

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SpringSubscriptionGraphQLContextFactory
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.socket.WebSocketSession
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.UserRole
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.security.JWTService
import tech.alexib.yaba.server.service.UserService
import tech.alexib.yaba.server.util.unauthorized
import java.util.UUID


class YabaGraphQLContext(
    request: ServerRequest,
    val userId: UserId? = null,
    val role: UserRole = UserRole.USER
) : SpringGraphQLContext(request) {
    fun id(): UserId = userId ?: unauthorized()
    fun isAdmin(): Boolean = if (role != UserRole.ADMIN) unauthorized() else true
}

class YabaSubscriptionGraphQLContext(
    val request: WebSocketSession,
    var token: String? = null
) : GraphQLContext

private val logger = KotlinLogging.logger { }

@Component
class YabaGraphqlContextFactory(private val userService: UserService, private val jwtService: JWTService) :
    SpringGraphQLContextFactory<YabaGraphQLContext>() {
    override suspend fun generateContext(request: ServerRequest): YabaGraphQLContext {

        val subject: Pair<UserId, UserRole>? =
            request.headers().firstHeader(HttpHeaders.AUTHORIZATION)?.let { authHeader ->
                //Check header length so that this does not throw if token is null
                if (authHeader.startsWith("Bearer") && authHeader.length > 9) {
                    val authToken = authHeader.substring(7)
                    val decoded = jwtService.decodeAccessToken(authToken)
                    val user = userService.findById(UUID.fromString(decoded.id).userId())
                    return@let when {
                        user == null -> null
                        !user.active -> null
                        else -> Pair(user.id, user.role)
                    }

//                if (userService.isUserActive(UUID.fromString(decoded.id).userId())) {
//                    return@let decoded.id
//                } else null

                } else null
            }

        return YabaGraphQLContext(
            request = request,
            userId = subject?.first,
            role = subject?.second ?: UserRole.USER
        )
    }
}


@Component
class YabaSubscriptionGraphQLContextFactory :
    SpringSubscriptionGraphQLContextFactory<YabaSubscriptionGraphQLContext>() {
    override suspend fun generateContext(request: WebSocketSession): YabaSubscriptionGraphQLContext {
        val token = request.attributes.entries
        token.forEach {
            logger.info { it }
        }
        return YabaSubscriptionGraphQLContext(request, null)
    }
}
