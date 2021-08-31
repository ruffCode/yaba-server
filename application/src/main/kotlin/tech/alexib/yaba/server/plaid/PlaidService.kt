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
package tech.alexib.yaba.server.plaid

import arrow.core.Either
import kotlinx.coroutines.delay
import kotlinx.datetime.toKotlinLocalDate
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.plaid.client.PlaidClient
import tech.alexib.plaid.client.infrastructure.BaseUrl
import tech.alexib.plaid.client.infrastructure.ClientId
import tech.alexib.plaid.client.infrastructure.PlaidApiConfiguration
import tech.alexib.plaid.client.infrastructure.PlaidVersion
import tech.alexib.plaid.client.infrastructure.Secret
import tech.alexib.plaid.client.model.AccessToken
import tech.alexib.plaid.client.model.AccountBase
import tech.alexib.plaid.client.model.AccountsGetRequest
import tech.alexib.plaid.client.model.CountryCode
import tech.alexib.plaid.client.model.InstitutionsGetByIdRequest
import tech.alexib.plaid.client.model.InstitutionsGetByIdRequestOptions
import tech.alexib.plaid.client.model.InstitutionsGetByIdResponse
import tech.alexib.plaid.client.model.InstitutionsGetRequest
import tech.alexib.plaid.client.model.InstitutionsGetRequestOptions
import tech.alexib.plaid.client.model.InstitutionsGetResponse
import tech.alexib.plaid.client.model.Item
import tech.alexib.plaid.client.model.ItemGetRequest
import tech.alexib.plaid.client.model.ItemGetResponse
import tech.alexib.plaid.client.model.ItemPublicTokenExchangeRequest
import tech.alexib.plaid.client.model.ItemPublicTokenExchangeResponse
import tech.alexib.plaid.client.model.ItemRemoveRequest
import tech.alexib.plaid.client.model.LinkTokenCreateRequest
import tech.alexib.plaid.client.model.LinkTokenCreateRequestUser
import tech.alexib.plaid.client.model.LinkTokenCreateResponse
import tech.alexib.plaid.client.model.PlaidError
import tech.alexib.plaid.client.model.Products
import tech.alexib.plaid.client.model.TransactionsGetRequest
import tech.alexib.plaid.client.model.TransactionsGetRequestOptions
import tech.alexib.plaid.client.model.TransactionsGetResponse
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.config.PlaidConfig
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

interface PlaidService {
    suspend fun createLinkToken(
        userId: UserId,
        accessToken: String? = null
    ): Either<PlaidError, LinkTokenCreateResponse>

    suspend fun exchangeToken(publicToken: String): ItemPublicTokenExchangeResponse

    suspend fun getAccountsForItem(accessToken: String): Either<PlaidError, List<AccountBase>>

    suspend fun removeItem(accessToken: String)

    suspend fun getInstitution(id: String): InstitutionsGetByIdResponse

    suspend fun fetchTransactions(
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
        accountIds: List<String>
    ): TransactionsGetResponse
}

@Component
class PlaidServiceImpl(private val plaidConfig: PlaidConfig) : PlaidService {

    private val plaidApiConfiguration = PlaidApiConfiguration(
        ClientId(plaidConfig.clientId),
        Secret(plaidConfig.secret),
        baseUrl = BaseUrl.Sandbox,
        plaidVersion = PlaidVersion("2020-09-14")
    )
    private val plaidClient = PlaidClient(plaidApiConfiguration)

    override suspend fun createLinkToken(
        userId: UserId,
        accessToken: String?
    ): Either<PlaidError, LinkTokenCreateResponse> {
        val request = LinkTokenCreateRequest(
            language = "en",
            clientName = "YABA Client",
            countryCodes = listOf(CountryCode.US),
            user = LinkTokenCreateRequestUser(
                clientUserId = userId.value.toString()
            ),
            products = listOf(Products.TRANSACTIONS, Products.AUTH),
            accessToken = accessToken,
            webhook = plaidConfig.hookUrl
        )
        return Either.catch {
            plaidClient.linkTokenCreate(request)
        }.mapLeft {
            it as PlaidError
        }
    }

    override suspend fun exchangeToken(publicToken: String): ItemPublicTokenExchangeResponse {
        val itemPublicTokenExchangeRequest =
            ItemPublicTokenExchangeRequest(publicToken = publicToken)

        return plaidClient
            .itemPublicTokenExchange(itemPublicTokenExchangeRequest)
    }

    suspend fun getItem(accessToken: AccessToken): Item {
        val itemGetRequest = ItemGetRequest(accessToken = accessToken)

        val itemGetResponse: ItemGetResponse = plaidClient
            .itemGet(itemGetRequest)

        return itemGetResponse.item
    }

    override suspend fun getAccountsForItem(accessToken: String): Either<PlaidError, List<AccountBase>> {
        return Either.catch {
            plaidClient.accountsGet(
                AccountsGetRequest(
                    accessToken = accessToken,

                )
            ).accounts
        }.mapLeft {
            it as PlaidError
        }
    }

    suspend fun institutions(count: Int = 200, offset: Int = 0): InstitutionsGetResponse {
        require(count < 2000)
        return plaidClient.institutionsGet(
            InstitutionsGetRequest(
                count = count,
                offset = offset,
                options = InstitutionsGetRequestOptions(
                    includeOptionalMetadata = true
                ),
                countryCodes = listOf(CountryCode.US)
            )
        )
    }

    override suspend fun getInstitution(id: String): InstitutionsGetByIdResponse {
        return plaidClient.institutionsGetById(
            InstitutionsGetByIdRequest(
                institutionId = id,
                countryCodes = listOf(CountryCode.US),
                options = InstitutionsGetByIdRequestOptions(
                    includeOptionalMetadata = true
                )
            )
        )
    }

    override suspend fun removeItem(accessToken: String) {
        plaidClient.itemRemove(
            ItemRemoveRequest(accessToken = accessToken)
        )
    }

    private suspend fun fetchTransactions(
        request: TransactionsGetRequest,
        count: Int,
        offset: Int,
        response: TransactionsGetResponse? = null
    ): TransactionsGetResponse {
        val result = plaidClient.transactionsGet(request)
        return if (result.transactions.size == count) {
            val nextOffset = offset + count
            val nextRequest = request.copy(
                options = request.options!!.copy(
                    offset = nextOffset
                )
            )
            val nextResult = result.copy(
                accounts = response?.accounts?.plus(result.accounts) ?: result.accounts,
                transactions = response?.transactions?.plus(result.transactions) ?: result.transactions
            )
            fetchTransactions(
                nextRequest,
                count,
                nextOffset,
                nextResult
            )
        } else {
            result.copy(
                accounts = response?.accounts?.plus(result.accounts) ?: result.accounts,
                transactions = response?.transactions?.plus(result.transactions) ?: result.transactions
            )
        }
    }

    override suspend fun fetchTransactions(
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
        accountIds: List<String>
    ): TransactionsGetResponse {
        val options = TransactionsGetRequestOptions(
            offset = 0,
            count = 100,
            accountIds = accountIds
        )
        val request = TransactionsGetRequest(
            accessToken = accessToken,
            startDate = startDate.toKotlinLocalDate(),
            endDate = endDate.toKotlinLocalDate(),
            options = options
        )

        return try {
            fetchTransactions(request, 100, 0)
        } catch (e: PlaidError) {
            when (e.errorType) {
                PlaidError.ErrorType.ITEM_ERROR -> {
                    if (e.errorCode == "PRODUCT_NOT_READY") {
                        logger.error { "PRODUCT NOT READY" }
                        delay(300)
                        fetchTransactions(request, 100, 0)
                    } else {
                        logger.error { e.errorMessage }
                        throw e
                    }
                }
                else -> {
                    logger.error { e.errorMessage }
                    throw e
                }
            }
        } catch (e: Throwable) {
            logger.error { e }
            throw e
        }
    }
}
