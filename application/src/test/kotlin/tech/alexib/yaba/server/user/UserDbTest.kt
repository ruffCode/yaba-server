package tech.alexib.yaba.server.user


import io.kotest.matchers.shouldBe
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.security.crypto.password.PasswordEncoder
import tech.alexib.yaba.server.UsersStub
import tech.alexib.yaba.server.config.BaseIntegrationTest
import tech.alexib.yaba.server.service.UserService
import tech.alexib.yaba.server.repository.LinkEventRepository
import tech.alexib.yaba.server.entity.LinkEvent
import tech.alexib.yaba.server.entity.LinkEventId
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.feature.user.toEntity
import java.util.UUID

private val logger = KotlinLogging.logger { }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserDbTest : BaseIntegrationTest() {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var linkEventRepository: LinkEventRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var connectionFactory: ConnectionFactory

    @BeforeAll
    fun setup() {
        initDb()
    }

    @AfterAll
    fun breakDown() {
        val client = DatabaseClient.create(connectionFactory)
        runBlocking {
            client.sql(
                """
            delete from users_table where id is not null 
        """.trimIndent()
            ).await()
        }
    }



    @Test
    fun `retrieves user by id`() {
        runBlocking {

            userRepository.findById(usersStub[1].id).fold({ exception ->
                fail(exception.message)

            }, { it.id.shouldBe(usersStub[1].id.value) })

        }
    }

    @Test
    fun `retrieves user by username`() {
        runBlocking {
            userRepository.findByEmail(usersStub[2].email).fold({
                fail(it.message)
            }, { it.id.shouldBe(usersStub[2].id.value) })
        }
    }

    @Test
    fun `inserts link event`() {
        val linkEvent = LinkEvent(
            userId = usersStub[2].id,
            linkSessionId = UUID.randomUUID().toString(),
            type = "success",
        )

        runBlocking {

            linkEventRepository.create(linkEvent.toEntity()).fold({
                fail("link event not inserted")
            }, {
                assertEquals(linkEvent.id, LinkEventId(it.id))
            })

        }
    }

    @Test
    fun `deletes user by id`() {
        runBlocking {
            userRepository.deleteUser(usersStub[0].id)
            userRepository.findById(usersStub.first().id).fold({}, { fail { "expected left" } })

        }
    }

    private fun initDb() {
        runBlocking {
            usersStub.forEach {
                userRepository.createUser(it.toEntity())
            }
        }
    }

}

val usersStub = UsersStub.users
