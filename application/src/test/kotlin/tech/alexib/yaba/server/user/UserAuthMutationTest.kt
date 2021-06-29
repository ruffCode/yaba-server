package tech.alexib.yaba.server.user

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import tech.alexib.yaba.server.config.DATA_JSON_PATH
import tech.alexib.yaba.server.config.GRAPHQL_ENDPOINT
import tech.alexib.yaba.server.config.GRAPHQL_MEDIA_TYPE
import tech.alexib.yaba.server.verifyError
import tech.alexib.yaba.server.verifyOnlyDataExists

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthMutationTest(@Autowired private val testClient: WebTestClient) {

    private val validEmail = "alexi5@aol.com"
    private val validPassword = "password12345"
    private val invalidPassword = "password"

    @Order(0)
    @Test
    fun `successfully register user`() {
        val query = "register"

        testClient.mutation(
            "mutation { $query(input:{email:\"$validEmail\",password:\"$validPassword\"}){id email token }}"
        )
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.token").isNotEmpty
    }

    @Order(1)
    @Test
    fun `successfully login in user`() {
        val query = "login"

        testClient.mutation(
            "mutation { $query(input:{email:\"$validEmail\",password:\"$validPassword\"}){id email token }}"
        )
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query.token").isNotEmpty
    }

    @Order(2)
    @Test
    fun `fails to login user with invalid credentials`() {
        val query = "login"

        testClient.mutation(
            "mutation { $query(input:{email:\"$validEmail\",password:\"$invalidPassword\"}){id email token }}"
        ).verifyError("UNAUTHORIZED")
    }
}


fun WebTestClient.mutation(body: String): WebTestClient.ResponseSpec = this.post()
    .uri(GRAPHQL_ENDPOINT)
    .accept(MediaType.APPLICATION_JSON)
    .contentType(GRAPHQL_MEDIA_TYPE)
    .bodyValue(body)
    .exchange()

