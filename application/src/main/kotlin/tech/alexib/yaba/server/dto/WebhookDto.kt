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
package tech.alexib.yaba.server.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime

sealed class WebhookRequest

@Serializable
enum class TransactionWebhookCode {
    /**
     * Fired when an Item's initial transaction pull is completed. Once this webhook has been fired,
     * transaction data for the most recent 30 days can be fetched for the Item.
     */
    INITIAL_UPDATE,

    /**
     * Fired when an Item's historical transaction pull is completed and Plaid has prepared as much historical
     * transaction data as possible for the Item. Once this webhook has been fired, transaction data beyond the most
     * recent 30 days can be fetched for the Item.
     */
    HISTORICAL_UPDATE,

    /**
     * Fired when new transaction data is available for an Item. Plaid will typically check for
     * new transaction data several times a day.
     */
    DEFAULT_UPDATE,

    /**
     * Fired when transaction(s) for an Item are deleted. The deleted transaction IDs are included in the
     * webhook payload. Plaid will typically check for deleted transaction data several times a day.
     */
    TRANSACTIONS_REMOVED
}

@Serializable
enum class WebhookType {
    TRANSACTIONS,
    AUTH,
    ITEM,
    INCOME,
    ASSETS
}

@Serializable
data class WebhookError(
    @SerialName("error_code")
    val errorCode: WebhookErrorCode,
    @SerialName("error_message")
    val errorMessage: String
)

@Serializable
enum class WebhookErrorCode {
    ITEM_LOGIN_REQUIRED,
    INVALID_CREDENTIALS,
    INVALID_MFA,
    INVALID_UPDATED_USERNAME,
    ITEM_LOCKED,
    ITEM_NO_ERROR,
    ITEM_NOT_SUPPORTED,
    ITEM_NO_VERIFICATION,
    INCORRECT_DEPOSIT_AMOUNTS,
    TOO_MANY_VERIFICATION_ATTEMPTS,
    USER_SETUP_REQUIRED,
    MFA_NOT_SUPPORTED,
    NO_ACCOUNTS,
    NO_AUTH_ACCOUNTS,
    PRODUCT_NOT_READY,
    PRODUCTS_NOT_SUPPORTED
}

@Serializable
data class ItemWebhookRequest(
    @SerialName("item_id")
    val itemId: String,
    @SerialName("webhook_code")
    val webhookCode: ItemWebhookCode,
    @SerialName("webhook_type")
    val webhookType: WebhookType,
    val error: ItemWebhookError? = null,
    @SerialName("consent_expiration_time")
    @Contextual
    val consentExpirationTime: OffsetDateTime? = null,
    @SerialName("new_webhook_url")
    val newWebhookUrl: String? = null
) : WebhookRequest()

@Serializable
enum class ItemWebhookCode {
    /**
     * Fired when an Item's webhook is updated. This will be sent to the newly specified webhook.
     */
    WEBHOOK_UPDATE_ACKNOWLEDGED,

    /**
     * Fired when an error is encountered with an Item. The error can be resolved by having the user go through
     * Link’s update mode.
     */
    ERROR,

    /**
     * The USER_PERMISSION_REVOKED webhook is fired to when an end user has used the my.plaid.com portal to
     * revoke the permission that they previously granted to access an Item. Once access to an Item has been revoked,
     * it cannot be restored. If the user subsequently returns to your application,
     * a new Item must be created for the user.
     */
    USER_PERMISSION_REVOKED,

    /**
     * Fired when an Item’s access consent is expiring in 7 days. Some Items have explicit expiration times
     * and we try to relay this when possible to reduce service disruption. This can be resolved by having the user
     * go through Link’s update mode.
     */
    PENDING_EXPIRATION
}

@Serializable
enum class ItemWebhookErrorType {
    INVALID_REQUEST, INVALID_RESULT, INVALID_INPUT, INSTITUTION_ERROR, RATE_LIMIT_EXCEEDED, API_ERROR, ITEM_ERROR,
    ASSET_REPORT_ERROR, RECAPTCHA_ERROR, OAUTH_ERROR, PAYMENT_ERROR, BANK_TRANSFER_ERROR
}

@Serializable
data class TransactionWebhookRequest(
    @SerialName("item_id")
    val itemId: String,
    @SerialName("new_transactions")
    val newTransactions: Int? = null,
    @SerialName("removed_transactions")
    val removedTransactions: List<String>? = emptyList(),
    @SerialName("webhook_code")
    val webhookCode: TransactionWebhookCode,
    @SerialName("webhook_type")
    val webhookType: WebhookType,
    val error: TransactionWebhookError? = null,
) : WebhookRequest()

@Serializable
data class ItemWebhookError(
    @SerialName("error_type")
    val errorType: ItemWebhookErrorType,
    @SerialName("error_code")
    val errorCode: String,
    @SerialName("error_message")
    val errorMessage: String,
    @SerialName("request_id")
    val requestId: String,
    val status: Int
)

@Serializable
data class TransactionWebhookError(
    @SerialName("error_type")
    val errorType: TransactionWebhookErrorType,
    @SerialName("error_code")
    val errorCode: String,
    @SerialName("error_message")
    val errorMessage: String,
    @SerialName("request_id")
    val requestId: String,
    val status: Int
)

@Serializable
data class GenericWebhookError(
    @SerialName("error_type")
    val errorType: String,
    @SerialName("error_code")
    val errorCode: String,
    @SerialName("error_message")
    val errorMessage: String,
    @SerialName("request_id")
    val requestId: String,
    val status: Int
)

@Serializable
enum class TransactionWebhookErrorType {
    INVALID_REQUEST, INVALID_RESULT, INVALID_INPUT, INSTITUTION_ERROR, RATE_LIMIT_EXCEEDED, API_ERROR, ITEM_ERROR,
    ASSET_REPORT_ERROR, RECAPTCHA_ERROR, OAUTH_ERROR, PAYMENT_ERROR, BANK_TRANSFER_ERROR
}

@Serializable
enum class AuthWebhookCode {
    AUTOMATICALLY_VERIFIED,
    VERIFICATION_EXPIRED
}

@Serializable
data class AuthWebhookRequest(
    @SerialName("type")
    val webhookType: WebhookType,
    @SerialName("webhook_code")
    val webhookCode: AuthWebhookCode,
    @SerialName("item_id")
    val itemId: String,
    @SerialName("account_id")
    val accountId: String,
    val error: GenericWebhookError? = null
) : WebhookRequest()

fun TransactionWebhookCode.toLocalDate(): LocalDate = when (this) {
    TransactionWebhookCode.INITIAL_UPDATE -> LocalDate.now().minusDays(30)
    TransactionWebhookCode.HISTORICAL_UPDATE -> LocalDate.now().minusYears(2L)
    TransactionWebhookCode.DEFAULT_UPDATE -> LocalDate.now().minusDays(14)
    else -> LocalDate.now().minusDays(30)
}
