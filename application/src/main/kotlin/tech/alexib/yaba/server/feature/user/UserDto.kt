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

package tech.alexib.yaba.server.feature.user

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.domain.common.UUIDSerializer
import tech.alexib.yaba.domain.user.UserRole
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.transaction.TransactionDto
import java.util.UUID

@GraphQLName("User")
@Serializable
data class UserDto(
    @Contextual
    val id: UUID,
    val email: String,
    val token: String? = null,
    @GraphQLIgnore
    val role: UserRole = UserRole.USER
) {
    @Transient
    @kotlinx.serialization.Transient
    lateinit var items: List<ItemDto>

    @Transient
    @kotlinx.serialization.Transient
    lateinit var accounts: List<AccountDto>

    @kotlin.jvm.Transient
    @kotlinx.serialization.Transient
    lateinit var transactions: List<TransactionDto>
}
