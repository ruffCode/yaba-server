package tech.alexib.yaba.server.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.yaba.domain.institution.CountryCode
import tech.alexib.yaba.domain.institution.Institution
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.institution.Product

import tech.alexib.yaba.server.dto.InstitutionDto

import java.util.UUID

@Table("institutions_table")
data class InstitutionEntity(
    @Id
    val id: UUID,
    val name: String,
    @Column("plaid_institution_id")
    val plaidInstitutionId: String,
    val products: String,
    @Column("country_codes")
    val countryCodes: String,
    val logo: String,
    @Column("primary_color")
    val primaryColor: String? = null,
    val url: String? = null,
    val oauth: Boolean,
    val routingNumbers: String? = null
) {
    fun toDto() = InstitutionDto(
        institutionId = plaidInstitutionId,
        name = name,
        products = products.split(",").map { Product.valueOf(it.trim()) },
        countryCodes = countryCodes.split(",").map { CountryCode.valueOf(it.trim()) },
        url = url,
        primaryColor = primaryColor,
        logo = logo,
        routingNumbers = routingNumbers?.split(","),
    )

    fun toDomain() = Institution(
        institutionId = InstitutionId(plaidInstitutionId),
        name = name,
        products = products.split(",").map { Product.valueOf(it.trim()) },
        countryCodes = countryCodes.split(",").map { CountryCode.valueOf(it.trim()) },
        url = url,
        primaryColor = primaryColor,
        logo = logo,
        routingNumbers = routingNumbers?.split(","),
        oauth = oauth
    )
}
