@file:UseSerializers(UUIDSerializer::class)

package tech.alexib.yaba.server.feature.user

import io.r2dbc.spi.Row
import kotlinx.serialization.UseSerializers
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.yaba.domain.common.UUIDSerializer
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.UserRole
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.security.JWTService
import tech.alexib.yaba.server.util.get
import java.time.OffsetDateTime
import java.util.UUID

@Table("users_table")
data class UserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String,
    val active: Boolean = true,
    val role: String = UserRole.USER.name,
    @Column("created_at")
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    companion object {
        fun toDomain(row: Row): User = User(
            id = UserId(row["id"] as UUID),
            email = row.get(User::email),
            password = row.get(User::password),
            createdAt = row.get(User::createdAt),
            updatedAt = row.get(User::updatedAt)
        )


        fun fromDomain(user: User) = UserEntity(
            id = user.id.value,
            email = user.email,
            password = user.password,
            role = user.role.name,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}

fun UserEntity.toDomain() = this.run {
    User(
        id = id.userId(),
        email = email,
        password = password,
        createdAt = createdAt,
        updatedAt = updatedAt,
        role = UserRole.valueOf(role)
    )
}

fun User.toEntity() = UserEntity.fromDomain(this)



fun User.toDto() = UserDto(
    id = id.value,
    email = email,
    token = token
)

