package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.repository.LinkEventRepository
import tech.alexib.yaba.server.dto.LinkEventInput
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated

private val logger = KotlinLogging.logger {}

@Component
class LinkEventMutation(
    private val linkEventRepository: LinkEventRepository
) : Mutation {

    @Authenticated
    suspend fun createLinkEvent(context: YabaGraphQLContext, input: LinkEventInput): Boolean {
        return linkEventRepository.create(input.toEntity(context.id())).fold({
            logger.error { "Duplicate Item ${input.requestId}" }
            false
        }, {
            true
        })
    }
}
