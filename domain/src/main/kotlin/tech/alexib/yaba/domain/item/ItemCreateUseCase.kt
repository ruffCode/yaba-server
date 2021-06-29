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
    validateItemCreation: ValidateItemCreation
): Either<ItemCreateError, Item> {
    val cmd = LinkItemCommand(this)
    return either {
        validateItemCreation(cmd).map { valid ->
            exchangePublicToken(valid.publicToken).flatMap { plaidResponse ->
                if (valid.relink) {
                    relinkItem(valid.institutionId, valid.userId, plaidResponse.accessToken).right()
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
