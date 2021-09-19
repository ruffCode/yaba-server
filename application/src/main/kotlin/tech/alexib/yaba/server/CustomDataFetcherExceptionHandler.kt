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
package tech.alexib.yaba.server

import graphql.GraphQLError
import graphql.GraphqlErrorException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import mu.KotlinLogging
import tech.alexib.yaba.server.util.YabaException
private val logger = KotlinLogging.logger {}
class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters):
        DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = when (exception) {
            is YabaException -> GraphqlErrorException.newErrorException()
                .cause(exception)
                .message(exception.message ?: "UNKNOWN ERROR")
                .sourceLocation(sourceLocation)
                .path(path.toList())
                .build()
            else ->
                GraphqlErrorException.newErrorException()
                    .cause(exception)
                    .message(exception.message)
                    .sourceLocation(sourceLocation)
                    .path(path.toList())
                    .build()
        }

//        val error = GraphqlErrorException.newErrorException()
//            .cause(exception)
//            .message(exception.message)
//            .sourceLocation(sourceLocation)
//            .path(path.toList())
//            .build()
        logger.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }
}
