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
package tech.alexib.yaba.domain.item

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.user.UserId
import java.util.UUID

@JvmInline
value class ItemId(val value: UUID)

@JvmInline
value class PlaidItemId(val value: String)

fun UUID.itemId() = ItemId(this)

@JvmInline
value class PlaidAccessToken(val value: String)

@JvmInline
value class PublicToken(val value: String)

data class PublicTokenExchangeResponse(
    val accessToken: PlaidAccessToken,
    val itemId: PlaidItemId
)

data class LinkItemCommand(
    val data: LinkItemRequest
)

data class LinkItemRequest(
    val publicToken: PublicToken,
    val institutionId: InstitutionId,
    val userId: UserId
)

data class ValidItemCreation(
    val publicToken: PublicToken,
    val institutionId: InstitutionId,
    val userId: UserId,
    val relink: Boolean
)

data class PlaidApiError(val message: String)
sealed class ItemCreateError {
    data class PlaidApiError(val message: String) : ItemCreateError()
    object ItemUnlinkedThreeTimesError : ItemCreateError()
}

fun <L> Boolean.toEither(ifFalse: () -> L): Either<L, Unit> = if (this) {
    Unit.right()
} else ifFalse().left()
