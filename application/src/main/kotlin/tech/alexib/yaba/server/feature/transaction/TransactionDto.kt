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
@file:UseSerializers(UUIDSerializer::class, JavaLocalDateSerializer::class)

package tech.alexib.yaba.server.feature.transaction

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.JavaLocalDateSerializer
import tech.alexib.yaba.domain.common.UUIDSerializer
import java.time.LocalDate
import java.util.UUID

@Serializable
@GraphQLName("Transaction")
data class TransactionDto(
    val accountId: UUID,
    val itemId: UUID,
    val category: String? = null,
    val subcategory: String? = null,
    val type: String,
    val name: String,
    val amount: Double,
    val userId: UUID,
    val isoCurrencyCode: String? = "USD",
    val unofficialCurrencyCode: String? = "",
    val date: LocalDate,
    val pending: Boolean,
    val id: UUID,
    val merchantName: String? = null
)
