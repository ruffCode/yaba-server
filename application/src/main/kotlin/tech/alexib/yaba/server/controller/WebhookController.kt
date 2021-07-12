package tech.alexib.yaba.server.controller

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

    private suspend fun handleItem(webhookRequest: ItemWebhookRequest) {
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

    private suspend fun handleTransactions(webhookRequest: TransactionWebhookRequest) {

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
                    transactionService.deleteTransactions(it,PlaidItemId(webhookRequest.itemId))
                }
            }
            else -> {
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


