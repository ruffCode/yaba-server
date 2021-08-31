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
package tech.alexib.yaba.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import tech.alexib.yaba.server.security.JWTReactiveAuthorizationFilter
import tech.alexib.yaba.server.security.JWTService
import tech.alexib.yaba.server.security.UserDetailsService

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebConfig : WebFluxConfigurer {

    companion object {
        val EXCLUDED_PATHS = arrayOf("/user/login", "user/register", "/user")
    }

    @Bean
    fun configureSecurity(
        http: ServerHttpSecurity,
        jwtAuthenticationWebFilter: AuthenticationWebFilter,
        jwtService: JWTService
    ): SecurityWebFilterChain {
        return http
            .authorizeExchange()
            .pathMatchers("/user/register")
            .permitAll()
            .pathMatchers("/user/login")
            .permitAll()
            .pathMatchers("/graphql")
            .permitAll()
            .pathMatchers("/playground")
            .permitAll()
            .pathMatchers("/subscriptions")
            .permitAll()
            .pathMatchers("/logo/*")
            .permitAll()
            .pathMatchers("/hook")
            .permitAll()
            .pathMatchers("/user")
            .authenticated()
            .anyExchange().authenticated()
            .and()
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(JWTReactiveAuthorizationFilter(jwtService), SecurityWebFiltersOrder.AUTHORIZATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .httpBasic()
            .disable()
            .csrf()
            .disable()
            .formLogin()
            .disable()
            .logout().disable()
            .build()
    }

    @Bean
    fun authenticationWebFilter(
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
        jwtConverter: ServerAuthenticationConverter,
//        serverAuthenticationSuccessHandler: ServerAuthenticationSuccessHandler,
        jwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler
    ): AuthenticationWebFilter {
        val authenticationWebFilter = AuthenticationWebFilter(reactiveAuthenticationManager)
        authenticationWebFilter.setRequiresAuthenticationMatcher {
            ServerWebExchangeMatchers.pathMatchers(
                HttpMethod.POST,
                "/user/login"
            ).matches(it)
        }
        authenticationWebFilter.setServerAuthenticationConverter(jwtConverter)
//        authenticationWebFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler)
        authenticationWebFilter.setAuthenticationFailureHandler(jwtServerAuthenticationFailureHandler)
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        return authenticationWebFilter
    }

    @Bean
    fun reactiveAuthenticationManager(
        reactiveUserDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager {
        val manager = UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService)
        manager.setPasswordEncoder(passwordEncoder)
        return manager
    }
}
