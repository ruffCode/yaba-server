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
package tech.alexib.yaba.server.security

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JWTReactiveAuthorizationFilter(private val jwtService: JWTService) : WebFilter {

    private val logger = KotlinLogging.logger { }

    @Suppress("ReturnCount")
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return chain.filter(exchange)

        if (!authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        try {
            val token = jwtService.decodeAccessToken(authHeader)
            val auth = UsernamePasswordAuthenticationToken(token.subject, null, jwtService.getRoles(token))
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        } catch (e: Exception) {
            logger.error("JWT exception", e)
        }

        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.clearContext())
    }
}
