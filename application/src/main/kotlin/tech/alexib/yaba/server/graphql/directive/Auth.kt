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