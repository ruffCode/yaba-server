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

//@Component
//class JwtAuthenticationManager(private val jwtService: JWTService) : ReactiveAuthenticationManager {
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
//}


