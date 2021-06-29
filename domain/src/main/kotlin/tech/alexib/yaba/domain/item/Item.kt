package tech.alexib.yaba.domain.item

import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.user.UserId
import java.time.OffsetDateTime
import java.util.UUID

data class Item(
    val id: ItemId = UUID.randomUUID().itemId(),
    val userId: UserId,
    val plaidAccessToken: String,
    val plaidInstitutionId: InstitutionId,
    val plaidItemId: String,
    val status: String,
    val linked: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    companion object
}
