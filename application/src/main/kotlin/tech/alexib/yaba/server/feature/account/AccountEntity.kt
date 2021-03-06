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
package tech.alexib.yaba.server.feature.account

import io.r2dbc.spi.Row
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.yaba.server.util.get
import tech.alexib.yaba.server.util.getProp
import java.time.OffsetDateTime
import java.util.UUID

@Table("accounts")
data class AccountEntity(
    @Column("item_id")
    val itemId: UUID,
    @Column("plaid_account_id")
    val plaidAccountId: String,
    val name: String,
    val mask: String,
    @Column("official_name")
    val officialName: String? = null,
    @Column("current_balance")
    val currentBalance: Double,
    @Column("available_balance")
    val availableBalance: Double? = null,
    @Column("credit_limit")
    val creditLimit: Double? = null,
    @Column("iso_currency_code")
    val isoCurrencyCode: String? = null,
    @Column("unofficial_currency_code")
    val unofficialCurrencyCode: String? = null,
    val type: String,
    val subtype: String,
    @Column("created_at")
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    @Id
    val id: UUID? = null,
    val hidden: Boolean = false,
) {

    companion object {
        fun fromRow(row: Row) = AccountEntity(
            id = row["id"] as UUID,
            itemId = row["item_id"] as UUID,
            plaidAccountId = row.get(AccountEntity::plaidAccountId),
            name = row.getProp("name"),
            mask = row.getProp("mask"),
            officialName = row.get(AccountEntity::officialName),
            currentBalance = row.get(AccountEntity::currentBalance),
            availableBalance = row.get(AccountEntity::availableBalance),
            isoCurrencyCode = row.get(AccountEntity::isoCurrencyCode),
            unofficialCurrencyCode = row.get(AccountEntity::unofficialCurrencyCode),
            type = row.getProp("type"),
            subtype = row.getProp("subtype"),
            hidden = row.getProp("hidden"),
            createdAt = row.get(AccountEntity::createdAt),
            updatedAt = row.get(AccountEntity::updatedAt),
        )
    }

    fun toDto() = AccountDto(
        id = id!!,
        itemId = itemId,
        plaidAccountId = plaidAccountId,
        name = name,
        mask = mask,
        officialName = officialName,
        currentBalance = currentBalance,
        availableBalance = availableBalance,
        creditLimit = creditLimit,
        isoCurrencyCode = isoCurrencyCode,
        unofficialCurrencyCode = unofficialCurrencyCode,
        type = AccountType.valueOf(type),
        subtype = AccountSubtype.valueOf(subtype),
        hidden = hidden
    )
}

@JvmInline
value class AccountId(val value: UUID)

fun UUID.accountId() = AccountId(this)

data class AccountInsertRequest(val account: tech.alexib.plaid.client.model.Account)

fun AccountInsertRequest.toEntity(itemId: UUID) = AccountEntity(

    itemId = itemId,
    plaidAccountId = account.accountId,
    name = account.name,
    mask = account.mask ?: "",
    officialName = account.officialName,
    currentBalance = account.balances.current,
    availableBalance = account.balances.available,
    creditLimit = account.balances.limit,
    type = account.type.name,
    subtype = account.subtype?.name ?: "",
    hidden = false
)

fun tech.alexib.plaid.client.model.Account.toEntity(itemId: UUID) = AccountEntity(
    itemId = itemId,
    plaidAccountId = accountId,
    name = name,
    mask = mask ?: "",
    officialName = officialName,
    currentBalance = balances.current,
    availableBalance = balances.available,
    creditLimit = balances.limit,
    type = type.name,
    subtype = subtype?.name ?: "",
)
