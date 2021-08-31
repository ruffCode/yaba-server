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

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import tech.alexib.yaba.server.feature.user.UserRepository

@Service
class UserDetailsService(private val userRepository: UserRepository) : ReactiveUserDetailsService {
    override fun findByUsername(email: String): Mono<UserDetails> = mono {
        return@mono userRepository.findByEmail(email)
            .fold({ throw BadCredentialsException("Invalid Credentials") }, {
                User(it.email, "password", listOf(SimpleGrantedAuthority("ROLE_USER")))
            })
    }
}
