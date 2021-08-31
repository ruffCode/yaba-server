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
package tech.alexib.yaba.server.graphql.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.feature.user.UserDto
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.util.unauthorized

@Component
class UserQuery(
    private val userRepository: UserRepository,
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
