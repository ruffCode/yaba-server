package tech.alexib.yaba.server.graphql.schema

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.GraphQLDirectiveContainer
import tech.alexib.yaba.server.graphql.directive.AdminSchemaDirectiveWiring
import tech.alexib.yaba.server.graphql.directive.AuthenticationSchemaDirectiveWiring
import java.util.Locale
import kotlin.reflect.KClass

class CustomDirectiveWiringFactory :
    KotlinDirectiveWiringFactory(
        manualWiring = mapOf(
            "auth" to AuthenticationSchemaDirectiveWiring(),
            "admin" to AdminSchemaDirectiveWiring()
        )
    ) {

    override fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>):
            KotlinSchemaDirectiveWiring? = null
}

internal fun getDirectiveName(kClass: KClass<out Annotation>): String = kClass.simpleName!!.replaceFirstChar {
    it.lowercase(
        Locale.getDefault()
    )
}
