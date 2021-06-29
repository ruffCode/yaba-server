package tech.alexib.yaba.domain.common

import arrow.core.left
import arrow.core.right

@JvmInline
value class AccessToken(val value: String)

sealed class DbResult<T> {

    data class Success<T>(
        val data: T
    ) : DbResult<T>()

    data class Error<T>(
        val exception: Throwable
    ) : DbResult<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> error(throwable: Throwable) = Error<T>(throwable)
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): DbResult<T> = runCatching {
    val result = block()
    DbResult.success(result)
}.getOrElse {
    DbResult.error<T>(it)
}

fun <T> dbQuery(block: () -> T): DbResult<T> = runCatching {
    val result = block()
    DbResult.success(result)
}.getOrElse {
    DbResult.error<T>(it)
}

fun <T, E> T?.toEither(e: E) = when (this) {
    null -> e.left()
    else -> this.right()
}
