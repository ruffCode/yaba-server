package tech.alexib.yaba.server.feature.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("last_login_table")
data class LastLogin(
    @Id
    @Column("user_id")
    val userId: UUID,
    @Column("last_login")
    val lastLogin: OffsetDateTime
)
