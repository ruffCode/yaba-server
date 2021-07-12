package tech.alexib.yaba.server.graphql.query

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.dto.InstitutionDto
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.repository.InstitutionRepository

@Component
class InstitutionQuery(
    private val plaidService: PlaidService,
    private val institutionRepository: InstitutionRepository
) : Query {

    @Authenticated
    suspend fun institutions(count: Int, offset: Int): List<InstitutionDto> =
        institutionRepository.findAll().map { it.toDto() }.toList()

    @Authenticated
    suspend fun institutionById(institutionId: String): InstitutionDto? =
        institutionRepository.findByPlaidInstitutionId(institutionId)?.toDto()

    @Authenticated
    suspend fun institutionsTop(): List<InstitutionDto> =
        institutionRepository.getTop().map { it.toDto() }.toList()

    @Authenticated
    suspend fun institutionsSearch(query: String): List<InstitutionDto> =
        institutionRepository.search(query).map { it.toDto() }.toList()

    @Authenticated
    suspend fun setLogos(): Boolean {
        institutionRepository.setLogos()
        return true
    }
}

