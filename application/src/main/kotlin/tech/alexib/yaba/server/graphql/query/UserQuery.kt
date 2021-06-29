package tech.alexib.yaba.server.graphql.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.service.UserService
import tech.alexib.yaba.server.util.unauthorized


@Component
class UserQuery(
    private val userRepository: UserRepository,
    private val userService: UserService
) : Query {

    @Authenticated
    suspend fun me(context: YabaGraphQLContext): UserDto {
        return userRepository.findById(context.id()).fold({
            throw unauthorized()
        }, {
            UserDto(
                email = it.email,
                id = it.id
            )
        })
    }


}


