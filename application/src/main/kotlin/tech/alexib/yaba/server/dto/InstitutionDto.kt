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
package tech.alexib.yaba.server.dto

import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.serialization.Serializable
import tech.alexib.yaba.domain.institution.CountryCode
import tech.alexib.yaba.domain.institution.Institution
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.institution.Product
import tech.alexib.yaba.server.entity.InstitutionEntity
import tech.alexib.yaba.server.repository.defaultLogo
import java.util.UUID

@GraphQLName("Institution")
@Serializable
data class InstitutionDto(
    val institutionId: String,
    val name: String,
    val products: List<Product>,
    val countryCodes: List<CountryCode>,
    val url: String? = null,
    val primaryColor: String? = null,
    val logo: String,
    val routingNumbers: List<String>? = null,
//    val oauth: Boolean,
)

fun tech.alexib.plaid.client.model.Institution.toDomain() = Institution(
    institutionId = InstitutionId(institutionId),
    name = name,
    products = products.map { Product.valueOf(it.name) },
    countryCodes = countryCodes.map { CountryCode.valueOf(it.name) },
    url = url,
    primaryColor = primaryColor,
    logo = logo ?: defaultLogo,
    routingNumbers = routingNumbers,
    oauth = oauth,
)

fun Institution.toDto() = InstitutionDto(
    institutionId = institutionId.value,
    name = name,
    products = products,
    countryCodes = countryCodes,
    url = url,
    primaryColor = primaryColor,
    logo = logo,
    routingNumbers = routingNumbers,
//    oauth = oauth,
)

fun Institution.toEntity() = InstitutionEntity(
    id = UUID.randomUUID(),
    name = name,
    plaidInstitutionId = institutionId.value,
    products = products.joinToString { it.name },
    countryCodes = countryCodes.joinToString { it.name },
    logo = logo,
    primaryColor = primaryColor,
    url = url,
    oauth = oauth,
    routingNumbers = routingNumbers?.joinToString()
)
