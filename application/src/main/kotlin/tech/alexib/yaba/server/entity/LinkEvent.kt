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
    companion object{
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


//{id=c7c567fd-15e4-491f-9af1-863fb4b9639e, type=success, user_id=bc324bc7-6acc-496a-93c0-9eb5417e66e6, link_session_id=8dded02b-dd79-4f08-a15d-9e84c6016ebf, request_id=null, error_type=null, error_code=null, created_at=2021-03-27T01:36:31.933775-04:00}
