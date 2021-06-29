package tech.alexib.yaba.domain.item

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
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



fun <L> Boolean.toEither(ifFalse: () -> L): Either<L, Unit> = if (this)
    Unit.right() else ifFalse().left()

