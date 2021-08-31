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
package tech.alexib.yaba.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter
import tech.alexib.yaba.domain.common.jSerializer
import tech.alexib.yaba.domain.item.PlaidItemId
import tech.alexib.yaba.server.dto.AuthWebhookRequest
import tech.alexib.yaba.server.dto.ItemWebhookCode
import tech.alexib.yaba.server.dto.ItemWebhookRequest
import tech.alexib.yaba.server.dto.TransactionWebhookCode
import tech.alexib.yaba.server.dto.TransactionWebhookRequest
import tech.alexib.yaba.server.dto.WebhookRequest
import tech.alexib.yaba.server.dto.WebhookType
import tech.alexib.yaba.server.dto.toLocalDate
import tech.alexib.yaba.server.service.TransactionService
import tech.alexib.yaba.server.util.respondOk
import java.time.LocalDate

@Configuration
class WebhookController(
    private val handler: WebhookHandler
) {
    @Bean
    fun hookRoutes() = coRouter {
        POST("hook", handler::handleWebhook)
    }
}

private val logger = KotlinLogging.logger {}

@Component
class WebhookHandler(
    private val transactionService: TransactionService
) {

    suspend fun handleWebhook(request: ServerRequest): ServerResponse {
        val rawRequest: String = request.awaitBody()
        logger.info { rawRequest }

        when (val webhookRequest: WebhookRequest = serializeWebhookRequest(rawRequest)) {
            is ItemWebhookRequest -> handleItem(webhookRequest)
            is TransactionWebhookRequest -> handleTransactions(webhookRequest)
            is AuthWebhookRequest -> {
            }
        }

        return respondOk("OK")
    }

    private fun handleItem(webhookRequest: ItemWebhookRequest) {
        when (webhookRequest.webhookCode) {
            ItemWebhookCode.PENDING_EXPIRATION -> logger.info {
                "Pending Expiration ${webhookRequest.itemId}" +
                    " ${webhookRequest.consentExpirationTime}"
            }
            ItemWebhookCode.USER_PERMISSION_REVOKED -> logger.info { "Permission revoked ${webhookRequest.itemId}" }
            ItemWebhookCode.ERROR -> logger.error {
                "Item Error ${webhookRequest.error}"
            }
            ItemWebhookCode.WEBHOOK_UPDATE_ACKNOWLEDGED ->
                logger.info { "Webhook updated ${webhookRequest.newWebhookUrl}" }
        }
    }

    private fun handleTransactions(webhookRequest: TransactionWebhookRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val startDate = webhookRequest.webhookCode.toLocalDate()
            when (webhookRequest.webhookCode) {
                TransactionWebhookCode.HISTORICAL_UPDATE, TransactionWebhookCode.DEFAULT_UPDATE -> {
                    transactionService.updateTransactions(
                        PlaidItemId(webhookRequest.itemId),
                        startDate,
                        LocalDate.now(),
                        webhookRequest.webhookCode == TransactionWebhookCode.DEFAULT_UPDATE
                    )
                }
                TransactionWebhookCode.TRANSACTIONS_REMOVED -> {
                    webhookRequest.removedTransactions?.let {
                        transactionService.deleteTransactions(it, PlaidItemId(webhookRequest.itemId))
                    }
                }
                else -> {
                }
            }
        }
    }
}

private fun serializeWebhookRequest(rawRequest: String): WebhookRequest {
    return when {
        rawRequest.contains(WebhookType.ITEM.name) -> jSerializer.decodeFromString(
            ItemWebhookRequest.serializer(),
            rawRequest
        )
        rawRequest.contains(WebhookType.TRANSACTIONS.name) -> jSerializer.decodeFromString(
            TransactionWebhookRequest.serializer(),
            rawRequest
        )
        rawRequest.contains(WebhookType.AUTH.name) -> jSerializer.decodeFromString(
            AuthWebhookRequest.serializer(),
            rawRequest
        )
        else -> throw IllegalArgumentException("not implemented")
    }
}
