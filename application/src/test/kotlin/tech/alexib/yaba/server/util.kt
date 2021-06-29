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


fun <Left,Right> assertIsError(either: Either<Left,Right>){
    when(either){
        is Either.Right -> fail("${either.value} should be an Error")
        is Either.Left -> pass()
    }
}
inline fun <Left,Right, reified Error> assertIsError(either: Either<Left, Right>, e:Error){

    either.fold({
               when(it){
                   is Error ->  assertTrue(true)
                   else -> fail { "expecting ${e!!::class.java} got ${it!!::class.java}"}
               }
    },{
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

private fun pass(){
    assertTrue(true)
}

//all credit to https://github.com/ExpediaGroup
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
