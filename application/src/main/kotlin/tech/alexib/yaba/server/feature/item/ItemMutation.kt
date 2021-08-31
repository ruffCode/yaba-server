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
package tech.alexib.yaba.server.feature.item

import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component
import tech.alexib.plaid.client.model.ItemPublicTokenExchangeResponse
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.itemId
import tech.alexib.yaba.server.feature.account.AccountEntity
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.graphql.context.YabaGraphQLContext
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.service.InstitutionService
import tech.alexib.yaba.server.service.ItemService
import tech.alexib.yaba.server.service.TransactionService
import tech.alexib.yaba.server.util.YabaException
import tech.alexib.yaba.server.util.toGraphql
import java.util.UUID

@Component
class ItemMutation(
    private val itemRepository: ItemRepository,
    private val plaidService: PlaidService,
    private val accountRepository: AccountRepository,
    private val institutionService: InstitutionService,
    private val transactionService: TransactionService,
    private val itemService: ItemService,
) : Mutation {

    @Suppress("LongMethod")
    @Authenticated
    suspend fun itemCreate(context: YabaGraphQLContext, input: ItemCreateInput): ItemCreateResponse {
        val userId = context.id()
        itemRepository.findByInstitutionId(
            input.institutionId,
            userId
        ).fold({
        }, {
            throw YabaException("You have already linked an item at this institution.").toGraphql()
        })

        val plaidResponse: ItemPublicTokenExchangeResponse = plaidService.exchangeToken(input.publicToken)

        val itemEntity = itemRepository.createItem(
            ItemEntity(
                id = UUID.randomUUID(),
                userId = userId.value,
                accessToken = plaidResponse.accessToken,
                institutionId = input.institutionId,
                plaidItemId = plaidResponse.itemId,
                status = "good",
            )
        ).fold({
            throw YabaException("You have already linked an item at this institution.").toGraphql()
        }, {
            it
        })

        val institution = institutionService.getOrCreate(InstitutionId(itemEntity.institutionId)).fold(
            {
                throw YabaException("Invalid institution id.").toGraphql()
            },
            {
                it
            }
        )
        val accounts = plaidService.getAccountsForItem(itemEntity.accessToken).fold({
            throw it
        }, {
            it.map { account ->
                val newAccount = AccountEntity(
                    itemId = itemEntity.id,
                    plaidAccountId = account.accountId,
                    name = account.name,
                    mask = account.mask ?: "0000",
                    officialName = account.officialName,
                    currentBalance = account.balances.current,
                    availableBalance = account.balances.available ?: 0.0,
                    isoCurrencyCode = account.balances.isoCurrencyCode,
                    unofficialCurrencyCode = account.balances.unofficialCurrencyCode,
                    type = account.type.name,
                    subtype = account.subtype?.name ?: "OTHER",
                    hidden = false,
                    id = UUID.randomUUID()
                )
                accountRepository.create(newAccount)
            }
        })

        return ItemCreateResponse(
            name = institution.name,
            itemId = itemEntity.id,
            accounts = accounts.map {
                AccountInfo(
                    plaidAccountId = it.plaidAccountId,
                    name = it.name,
                    mask = it.mask
                )
            },
            logo = institution.logo
        )
    }

    @Authenticated
    suspend fun itemUpdateStatus(status: String, itemId: UUID): ItemDto {
        if (status !in listOf("good", "bad")) {
            throw YabaException(
                "Cannot set item status. Please use an 'good' or 'bad'",
            )
        }
        return itemRepository.updateStatus(itemId, status).toDto()
    }

    @Authenticated
    suspend fun itemUnlink(context: YabaGraphQLContext, itemId: UUID): Boolean {
        itemService.unlinkItem(itemId.itemId(), context.id())

//        itemRepository.findById(itemId.itemId()).fold({
//            notFound("Item not found for id $itemId")
//        }, {
//            plaidService.removeItem(it.accessToken)
//        })
//
//        itemRepository.delete(itemId)
        return true
    }

    @Authenticated
    suspend fun setAccountsToHide(plaidAccountIds: List<String>, itemId: UUID): Boolean {
        plaidAccountIds.forEach {
            accountRepository.setHidden(it, true)
        }
        transactionService.initial(ItemId(itemId))
        return true
    }
}

data class ItemCreateInput(
    val publicToken: String,
    val institutionId: String,
)

data class AccountInfo(
    val plaidAccountId: String,
    val name: String,
    val mask: String,
)

data class ItemCreateResponse(
    val name: String,
    val itemId: UUID,
    val logo: String,
    val accounts: List<AccountInfo>,
)
