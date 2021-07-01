package tech.alexib.yaba.server.feature.transaction

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("categories")
data class CategoryEntity(
    @Id
    val id: Long,
    @Column("grp")
    val group: String,
    val hierarchy: List<String>
)