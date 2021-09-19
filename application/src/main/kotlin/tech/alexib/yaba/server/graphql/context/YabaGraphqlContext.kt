/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.server.graphql.context

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SpringSubscriptionGraphQLContextFactory
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
    val role: UserRole = UserRole.USER,
) : SpringGraphQLContext(request) {
    fun id(): UserId = userId ?: unauthorized()
    fun isAdmin(): Boolean = if (role != UserRole.ADMIN) unauthorized() else true
}

class YabaSubscriptionGraphQLContext(
    val request: WebSocketSession,
    var token: String? = null,
) : GraphQLContext

@Component
class YabaGraphqlContextFactory(private val userService: UserService, private val jwtService: JWTService) :
    SpringGraphQLContextFactory<YabaGraphQLContext>() {
    override suspend fun generateContext(request: ServerRequest): YabaGraphQLContext {
        val subject: Pair<UserId, UserRole>? =
            request.headers().firstHeader(HttpHeaders.AUTHORIZATION)?.let { authHeader ->
                // Check header length so that this does not throw if token is null
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
        return YabaSubscriptionGraphQLContext(request, null)
    }
}
