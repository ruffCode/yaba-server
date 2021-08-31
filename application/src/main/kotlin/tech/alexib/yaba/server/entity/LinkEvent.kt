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
package tech.alexib.yaba.server.entity

import io.r2dbc.spi.Row
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.util.get
import java.time.OffsetDateTime
import java.util.UUID

@Table("link_events_table")
data class LinkEventEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val type: String,
    @Column("user_id")
    val userId: UUID,
    @Column("link_session_id")
    val linkSessionId: String,
    @Column("request_id")
    val requestId: String? = null,
    @Column("error_type")
    val errorType: String? = null,
    @Column("error_code")
    val errorCode: String? = null,
    @CreatedDate
    @Column("created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    companion object {
        fun toDomain(row: Row): LinkEvent {
            return LinkEvent(
                id = (row["id"] as UUID).toLinkEventId(),
                type = row.get(LinkEvent::type),
                userId = (row["user_id"] as UUID).userId(),
                linkSessionId = row.get(LinkEvent::linkSessionId),
                requestId = row.get(LinkEvent::requestId),
                errorCode = row.get(LinkEvent::errorCode),
                errorType = row.get(LinkEvent::errorType),
                createdAt = row.get(LinkEvent::createdAt)
            )
        }
    }

    fun toDomain(): LinkEvent = LinkEvent(
        id = id.toLinkEventId(),
        type = type,
        userId = userId.userId(),
        linkSessionId = linkSessionId,
        requestId = requestId,
        errorType = errorType,
        errorCode = errorCode,
        createdAt = createdAt
    )
}

data class LinkEvent(
    val id: LinkEventId = UUID.randomUUID().toLinkEventId(),
    val type: String,
    val userId: UserId,
    val linkSessionId: String,
    val requestId: String? = null,
    val errorType: String? = null,
    val errorCode: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    companion object

    fun toEntity(): LinkEventEntity = LinkEventEntity(
        id = id.value,
        type = type,
        userId = userId.value,
        linkSessionId = linkSessionId,
        requestId = requestId,
        errorType = errorType,
        errorCode = errorCode,
        createdAt = createdAt
    )
}

@JvmInline
value class LinkEventId(val value: UUID)

fun UUID.toLinkEventId() = LinkEventId(this)
