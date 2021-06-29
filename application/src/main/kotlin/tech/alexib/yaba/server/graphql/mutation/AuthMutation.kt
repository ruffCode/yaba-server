package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.server.operations.Mutation
import graphql.GraphqlErrorException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.LoginRequest
import tech.alexib.yaba.domain.user.RegisterUserRequest
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.server.service.UserService
import tech.alexib.yaba.server.util.YabaException
import tech.alexib.yaba.server.util.unauthorized
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class AuthMutation(
    private val userService: UserService
) : Mutation {

    suspend fun login(input: LoginRequest): UserWithTokenResponse {
        val user = userService.login(input)
        val token = user.token ?: throw unauthorized()
        return user.userWithTokenResponse(token)
    }

    suspend fun register(input: UserRegisterInput): UserWithTokenResponse {

        return runCatching {
            val user = userService.register(input.toRegisterRequest())
            val token = user.token ?: throw unauthorized()
            user.userWithTokenResponse(token)
        }.getOrElse {
            when (it) {
                is YabaException -> {
                    logger.info { "yaba error  ${it.message} " }
                    throw GraphqlErrorException.newErrorException().message("yaba reg error").build()
                }
                else -> throw GraphqlErrorException.newErrorException().message("reg error ${it.localizedMessage}")
                    .build()
            }
        }
    }
}

data class UserWithTokenResponse(
    val id: UUID,
    val email: String,
    val token: String
)

data class UserRegisterInput(
    val email: String,
    val password: String
)

fun UserRegisterInput.toRegisterRequest() = RegisterUserRequest(Email(email), password)

fun User.userWithTokenResponse(token: String) = UserWithTokenResponse(id = id.value, email = email, token = token)
