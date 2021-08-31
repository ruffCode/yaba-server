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
@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.server.feature.account

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.transaction.TransactionDto
import java.util.UUID

@Serializable
@GraphQLName("Account")
data class AccountDto(
    @Contextual
    val id: UUID,
    @Contextual
    val itemId: UUID,
    val plaidAccountId: String,
    val name: String,
    val mask: String,
    val officialName: String? = null,
    val currentBalance: Double,
    val availableBalance: Double? = null,
    val creditLimit: Double? = null,
    val isoCurrencyCode: String? = null,
    val unofficialCurrencyCode: String? = null,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false

) {
    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var item: ItemDto

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var transactions: List<TransactionDto>
}

@Serializable
enum class AccountType {
    DEPOSITORY,
    CREDIT,
    INVESTMENT,
    LOAN
}

@Serializable
enum class AccountSubtype {
    CHECKING,
    SAVINGS,
    CD,
    CREDIT_CARD,
    MONEY_MARKET,
    IRA,
    FOUR_HUNDRED_ONE_K,
    STUDENT,
    MORTGAGE
}
