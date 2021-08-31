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
package tech.alexib.yaba.server.util

import graphql.GraphqlErrorException

class YabaException(message: String?) : Exception(message)

fun badRequest(message: String = "bad request"): Nothing = throw YabaException(message).toGraphql()
fun serverError(message: String = "unknown server error"): Nothing = throw YabaException(message).toGraphql()
fun notFound(message: String = "not found"): Nothing = throw YabaException(message).toGraphql()
fun unauthorized(): Nothing = throw YabaException("UNAUTHORIZED").toGraphql()

fun Throwable.toGraphql() =
    GraphqlErrorException.newErrorException().message(this.localizedMessage).build()

fun graphqlError(message: String): Nothing = throw GraphqlErrorException.newErrorException().message(message).build()
