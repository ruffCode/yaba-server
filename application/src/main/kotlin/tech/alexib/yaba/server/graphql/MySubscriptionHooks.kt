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
package tech.alexib.yaba.server.graphql

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import org.springframework.web.reactive.socket.WebSocketSession
import tech.alexib.yaba.server.graphql.context.YabaSubscriptionGraphQLContext

// @Component
// class SimpleSubscription : Subscription {
//
//    val logger: Logger = LoggerFactory.getLogger(SimpleSubscription::class.java)

//    @GraphQLDescription("Returns a single value")
//    fun singleValueSubscription(): Flux<Int> = Flux.just(1)
//
//    @GraphQLDescription("Returns a random number every second")
//    fun counter(limit: Long? = null): Flux<Int> {
//        val flux = Flux.interval(Duration.ofSeconds(1)).map {
//            val value = Random.nextInt()
//            logger.info("Returning $value from counter")
//            value
//        }
//
//        return if (limit != null) {
//            flux.take(limit)
//        } else {
//            flux
//        }
//    }
//
//    @GraphQLDescription("Returns a random number every second, errors if even")
//    fun counterWithError(): Flux<Int> = Flux.interval(Duration.ofSeconds(1))
//        .map {
//            val value = Random.nextInt()
//            if (value % 2 == 0) {
//                throw Exception("Value is even $value")
//            } else value
//        }
//
//    @GraphQLDescription("Returns one value then an error")
//    fun singleValueThenError(): Flux<Int> = Flux.just(1, 2)
//        .map { if (it == 2) throw Exception("Second value") else it }
//
//    @GraphQLDescription("Returns stream of values")
//    fun flow(): Publisher<Int> = listOf(1, 2, 4, 5).asFlow().map {
//        delay(2000)
//        it
//    }.asPublisher()
//
//    @GraphQLDescription("Returns stream of errors")
//    fun flowOfErrors(): Publisher<DataFetcherResult<String?>> {
//        val dfr: DataFetcherResult<String?> = DataFetcherResult.newResult<String?>()
//            .data(null)
//            .error(GraphqlErrorException.newErrorException().cause(Exception("error thrown")).build())
//            .build()
//
//        return flowOf(dfr, dfr).asPublisher()
//    }
//
//    @GraphQLDescription("Returns a value from the subscription context")
//    fun subscriptionContext(myGraphQLContext: YabaSubscriptionGraphQLContext): Flux<String> =
//        Flux.just(myGraphQLContext.token ?: "no-auth")
// }

class MySubscriptionHooks : ApolloSubscriptionHooks {

    override fun onConnect(
        connectionParams: Map<String, String>,
        session: WebSocketSession,
        graphQLContext: GraphQLContext?,
    ): GraphQLContext? {
        if (graphQLContext != null && graphQLContext is YabaSubscriptionGraphQLContext) {
            graphQLContext.token = connectionParams["Authorization"]
        }
        return graphQLContext
    }
}
