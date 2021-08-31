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
