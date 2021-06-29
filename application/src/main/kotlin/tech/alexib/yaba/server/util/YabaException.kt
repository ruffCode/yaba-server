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
