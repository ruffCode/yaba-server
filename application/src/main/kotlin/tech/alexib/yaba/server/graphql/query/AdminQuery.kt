package tech.alexib.yaba.server.graphql.query

import com.expediagroup.graphql.server.operations.Query
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.user.UserRepository
import tech.alexib.yaba.server.graphql.directive.Admin
import tech.alexib.yaba.server.plaid.PlaidService
import java.util.UUID

@Component
class AdminQuery(
    private val connectionFactory: ConnectionFactory,
    private val itemRepository: ItemRepository,
    private val plaidService: PlaidService,
    private val userRepository: UserRepository
) : Query {

    private val client by lazy {
        DatabaseClient.create(connectionFactory)
    }

    @Admin
    suspend fun cleanUpUsers(testUser: Boolean): Boolean {
    val userIds = client.sql(
            """
            select plaid_access_token,u.id from users u
            join items i on u.id = i.user_id
            where u.email like '%aol.com';
        """.trimIndent()
        ).map { row ->
            Pair(
                row["plaid_access_token"] as String,
                row["id"] as UUID
            )
        }.flow().map {
            plaidService.removeItem(it.first)
            itemRepository.deleteByAccessToken(it.first)
            if (testUser) it.second else null
        }.toList()

        userIds.filterNotNull().toSet().forEach {
            userRepository.deleteUser(it.userId())
        }
        return true
    }
}