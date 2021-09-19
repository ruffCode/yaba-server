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
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import java.util.UUID

suspend inline fun LinkItemCommand.validate(
    getItemByInstitutionIdWithTimesUnlinked: GetItemByInstitutionIdWithTimesUnlinked,
): Either<ItemCreateError, ValidItemCreation> {
    return either {
        getItemByInstitutionIdWithTimesUnlinked(data.institutionId, data.userId).fold({ (_, timesUnlinked) ->
            if (timesUnlinked >= 3) {
                ItemCreateError.ItemUnlinkedThreeTimesError.left()
            } else {
                ValidItemCreation(data.publicToken, data.institutionId, data.userId, true).right()
            }
        }, {
            ValidItemCreation(data.publicToken, data.institutionId, data.userId, false).right()
        }).bind()
    }
}

suspend inline fun LinkItemRequest.create(
    exchangePublicToken: ExchangePublicToken,
    createItem: CreateItem,
    relinkItem: RelinkItem,
    validateItemCreation: ValidateItemCreation,
): Either<ItemCreateError, Item> {
    val cmd = LinkItemCommand(this)
    return either {
        validateItemCreation(cmd).map { valid ->
            exchangePublicToken(valid.publicToken).flatMap { plaidResponse ->
                if (valid.relink) {
                    relinkItem(
                        valid.institutionId,
                        valid.userId,
                        plaidResponse.accessToken,
                        plaidResponse.itemId
                    ).right()
                } else {
                    val item = Item(
                        id = UUID.randomUUID().itemId(),
                        userId = valid.userId,
                        plaidAccessToken = plaidResponse.accessToken.value,
                        plaidInstitutionId = valid.institutionId,
                        plaidItemId = plaidResponse.itemId.value,
                        status = "good",
                    )
                    createItem(item).right()
                }
            }.mapLeft { ItemCreateError.PlaidApiError(it.message) }.bind()
        }.bind()
    }
}
