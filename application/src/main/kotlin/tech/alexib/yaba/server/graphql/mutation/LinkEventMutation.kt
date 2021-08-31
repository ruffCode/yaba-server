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
package tech.alexib.yaba.server.graphql.mutation

import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.dto.LinkEventInput
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.repository.LinkEventRepository

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
