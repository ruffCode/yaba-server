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
