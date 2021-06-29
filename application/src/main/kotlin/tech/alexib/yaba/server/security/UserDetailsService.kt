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
