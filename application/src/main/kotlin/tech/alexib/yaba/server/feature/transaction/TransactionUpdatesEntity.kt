package tech.alexib.yaba.server.feature.transaction

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("transaction_updates")
data class TransactionUpdatesEntity(
    @Column("user_id")
    val userId: UUID,
    val added: List<String>? = null,
    val removed: List<String>? = null,
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column("created_at")
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
)
