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
package tech.alexib.yaba.server.feature.transaction

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Table("transactions")
data class TransactionEntity(
    @Column("account_id")
    val accountId: UUID,
    @Column("plaid_transaction_id")
    val plaidTransactionId: String,
    @Column("plaid_account_id")
    val plaidAccountId: String,
    @Column("plaid_item_id")
    val plaidItemId: String,
    @Column("item_id")
    val itemId: UUID,
    val category: String? = null,
    val subcategory: String? = null,
    val type: String,
    val name: String,
    val amount: Double,
    @Column("user_id")
    val userId: UUID,
    @Column("iso_currency_code")
    val isoCurrencyCode: String? = "USD",
    @Column("unofficial_currency_code")
    val unofficialCurrencyCode: String? = "",
    val date: LocalDate,
    val pending: Boolean,
    @Column("account_owner")
    val accountOwner: String,
    @Column("created_at")
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    @Id
    val id: UUID? = null,
    @Column("merchant_name")
    val merchantName: String? = null,
) {
    fun toDto() = TransactionDto(
        id = id!!,
        accountId = accountId,
        itemId = itemId,
        category = category,
        subcategory = subcategory,
        type = type,
        name = name,
        amount = amount,
        userId = userId,
        isoCurrencyCode = isoCurrencyCode,
        unofficialCurrencyCode = unofficialCurrencyCode,
        date = date,
        pending = pending,
        merchantName = merchantName
    )
}
