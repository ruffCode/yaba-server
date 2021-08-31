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
package tech.alexib.yaba.server.graphql.directive

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext

@GraphQLDirective(name = "auth", description = "Checks that there is valid auth token and the user is active")
annotation class Authenticated

class AuthenticationSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher = environment.getDataFetcher()

        val authFetcher = DataFetcher { dataEnv ->
            dataEnv.getContext<YabaGraphQLContext>().id()
            originalDataFetcher.get(dataEnv)
        }
        environment.setDataFetcher(authFetcher)
        return field
    }
}

@GraphQLDirective(name = "admin", "Checks if user is an admin")
annotation class Admin

class AdminSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher = environment.getDataFetcher()
        val adminAuthFetcher = DataFetcher { dataEnv ->
            val context = dataEnv.getContext<YabaGraphQLContext>()
            context.id()
            context.isAdmin()
            originalDataFetcher.get(dataEnv)
        }
        environment.setDataFetcher(adminAuthFetcher)
        return field
    }
}
