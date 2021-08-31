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
package tech.alexib.yaba.server.feature.item

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.Item
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.user.UserId
import java.time.OffsetDateTime
import java.util.UUID

@Table("items")
data class ItemEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column("user_id")
    val userId: UUID,
    @Column("plaid_access_token")
    val accessToken: String,
    @Column("plaid_institution_id")
    val institutionId: String,
    @Column("plaid_item_id")
    val plaidItemId: String,
    val status: String,
    val linked: Boolean = true,
    @Column("times_unlinked")
    val timesUnlinked: Int = 0,
    @CreatedDate
    @Column("created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
) {

    fun toDto() = ItemDto(
        id = id,
        userId = UserId(value = userId),
        plaidInstitutionId = institutionId,
        status = status
    )

    fun toDomain() = Item(
        id = ItemId(value = id),
        userId = UserId(value = userId),
        plaidAccessToken = accessToken,
        plaidInstitutionId = InstitutionId(institutionId),
        plaidItemId = plaidItemId,
        status = status,
    )
}

fun Item.toEntity() = ItemEntity(
    id = id.value,
    userId = userId.value,
    accessToken = plaidAccessToken,
    institutionId = plaidInstitutionId.value,
    plaidItemId = plaidItemId,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)
