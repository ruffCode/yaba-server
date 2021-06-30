package tech.alexib.yaba.server.feature.account

import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.graphql.directive.Authenticated
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class AccountMutation(
    private val accountRepository: AccountRepository
) : Mutation {

    @Authenticated
    suspend fun setAccountHidden(input: SetAccountHiddenInput): Boolean {
        return runCatching {
            accountRepository.setHidden(input.accountId, input.hide)
            true
        }.getOrElse {
            logger.error {
                "error setting account hidden ${it.localizedMessage}"

            }
            false
        }
    }
}


data class SetAccountHiddenInput(
    val accountId: UUID,
    val hide: Boolean
)