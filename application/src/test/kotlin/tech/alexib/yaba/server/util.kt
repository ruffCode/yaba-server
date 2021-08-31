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

import arrow.core.Either
import arrow.core.computations.either
import io.kotest.assertions.fail
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.springframework.test.web.reactive.server.WebTestClient
import tech.alexib.yaba.server.config.DATA_JSON_PATH
import tech.alexib.yaba.server.config.ERRORS_JSON_PATH
import tech.alexib.yaba.server.config.EXTENSIONS_JSON_PATH

fun <Left, Right> assertIsError(either: Either<Left, Right>) {
    when (either) {
        is Either.Right -> fail("${either.value} should be an Error")
        is Either.Left -> pass()
    }
}
inline fun <Left, Right, reified Error> assertIsError(either: Either<Left, Right>, e: Error) {
    either.fold({
        when (it) {
            is Error -> assertTrue(true)
            else -> fail { "expecting ${e!!::class.java} got ${it!!::class.java}" }
        }
    }, {
        fail("$it should be an Error")
    })
}

fun <Left, Right> assertOnOkValue(
    either: Either<Left, Right>,
    assertWhenOk: (Right) -> Unit
) {
    when (either) {
        is Either.Right -> assertWhenOk(either.value)
        is Either.Left -> fail("${either.value} is not Ok")
    }
}

fun <Left, Right> assertIsOk(either: Either<Left, Right>) {
    when (either) {
        is Either.Right -> pass()
        is Either.Left -> fail("${either.value} is not Ok")
    }
}

private fun pass() {
    assertTrue(true)
}

// all credit to https://github.com/ExpediaGroup
fun WebTestClient.ResponseSpec.verifyOnlyDataExists(expectedQuery: String): WebTestClient.BodyContentSpec {
    return this.expectBody()
        .jsonPath("$DATA_JSON_PATH.$expectedQuery").exists()
        .jsonPath(ERRORS_JSON_PATH).doesNotExist()
        .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
}

fun WebTestClient.ResponseSpec.verifyData(
    expectedQuery: String,
    expectedData: String
): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .verifyOnlyDataExists(expectedQuery)
        .jsonPath("$DATA_JSON_PATH.$expectedQuery").isEqualTo(expectedData)
}

fun WebTestClient.ResponseSpec.verifyError(expectedError: String): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .expectBody()
        .jsonPath(DATA_JSON_PATH).doesNotExist()
        .jsonPath("$ERRORS_JSON_PATH.[0].message").isEqualTo(expectedError)
        .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
}
