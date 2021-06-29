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
    suspend operator fun invoke(institutionId: InstitutionId, userId: UserId, plaidAccessToken: PlaidAccessToken): Item
}

fun interface ValidateItemCreation {
    suspend operator fun invoke(command: LinkItemCommand): Either<ItemCreateError, ValidItemCreation>
}
