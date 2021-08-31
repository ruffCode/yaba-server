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
