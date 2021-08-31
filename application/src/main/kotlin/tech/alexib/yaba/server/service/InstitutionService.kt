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
package tech.alexib.yaba.server.service

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.institution.Institution
import tech.alexib.yaba.domain.institution.InstitutionError
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.server.dto.toDomain
import tech.alexib.yaba.server.dto.toEntity
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.repository.InstitutionRepository

interface InstitutionService {
    suspend fun getOrCreate(institutionId: InstitutionId): Either<InstitutionError, Institution>
}

@Service
class InstitutionServiceImpl(
    private val institutionRepository: InstitutionRepository,
    private val plaidService: PlaidService
) : InstitutionService {

    override suspend fun getOrCreate(institutionId: InstitutionId): Either<InstitutionError, Institution> {
        val existing = getFromDbById(institutionId)

        return either {
            existing?.right()?.bind() ?: getFromPlaidById(institutionId).map {
                saveInstitution(it)
            }.bind()
        }
    }

    private suspend fun getFromDbById(institutionId: InstitutionId): Institution? =
        institutionRepository.findByPlaidInstitutionId(institutionId.value)?.toDomain()

    private suspend fun getFromPlaidById(institutionId: InstitutionId):
        Either<InstitutionError.InvalidInstitutionId, Institution> =
        runCatching {
            plaidService.getInstitution(institutionId.value).institution.toDomain().right()
        }.getOrElse { InstitutionError.InvalidInstitutionId.left() }

    private suspend fun saveInstitution(institution: Institution): Institution =
        institutionRepository.insert(institution.toEntity()).toDomain()
}
