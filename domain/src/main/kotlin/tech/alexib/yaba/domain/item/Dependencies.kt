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
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.user.UserId

fun interface ExchangePublicToken {
    suspend operator fun invoke(publicToken: PublicToken): Either<PlaidApiError, PublicTokenExchangeResponse>
}

fun interface CreateItem {
    suspend operator fun invoke(item: Item): Item
}

fun interface GetItemByInstitutionIdWithTimesUnlinked {
    suspend operator fun invoke(institutionId: InstitutionId, userId: UserId): Either<Pair<Item, Int>, Unit>
}

fun interface RelinkItem {
    suspend operator fun invoke(
        institutionId: InstitutionId,
        userId: UserId,
        plaidAccessToken: PlaidAccessToken,
        plaidItemId: PlaidItemId,
    ): Item
}

fun interface ValidateItemCreation {
    suspend operator fun invoke(command: LinkItemCommand): Either<ItemCreateError, ValidItemCreation>
}
