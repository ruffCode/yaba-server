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
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.security.JWTService
import tech.alexib.yaba.server.service.UserService
import tech.alexib.yaba.server.util.unauthorized
import java.util.UUID


class YabaGraphQLContext(
    request: ServerRequest,
    val userId: UserId? = null
) : SpringGraphQLContext(request) {
    fun id(): UserId = userId ?: unauthorized()
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

        val subject = request.headers().firstHeader(HttpHeaders.AUTHORIZATION)?.let { authHeader ->
            //Check header length so that this does not throw if token is null
            if (authHeader.startsWith("Bearer") && authHeader.length > 9) {
                val authToken = authHeader.substring(7)
                val decoded = jwtService.decodeAccessToken(authToken)
                if (userService.isUserActive(UUID.fromString(decoded.id).userId())) {
                    return@let decoded.id
                } else null

            } else null
        }

        return YabaGraphQLContext(
            request = request,
            userId = subject?.let { UserId(UUID.fromString(it)) }
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
