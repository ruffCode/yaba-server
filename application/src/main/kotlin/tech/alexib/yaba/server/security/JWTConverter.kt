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

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class JWTConverter : ServerAuthenticationConverter {
    override fun convert(swe: ServerWebExchange?): Mono<Authentication> {
        swe?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION)?.let { authHeader ->
            if (authHeader.startsWith("Bearer ")) {
                val authToken = authHeader.substring(7)
                return UsernamePasswordAuthenticationToken(authToken, authToken).toMono()
            }
        }
        return Mono.empty()
    }
}

// @Component
// class JwtAuthenticationManager(private val jwtService: JWTService) : ReactiveAuthenticationManager {
//    override fun authenticate(authentication: Authentication): Mono<Authentication> {
//        return Mono.just(authentication)
//            .map { jwtService.decodeAccessToken(it.credentials as String) }
//            .onErrorResume { Mono.empty() }
//            .map { jws ->
//                UsernamePasswordAuthenticationToken(
//                    jws.subject,
//                    authentication.credentials as String,
//                    mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
//                )
//            }
//    }
// }
