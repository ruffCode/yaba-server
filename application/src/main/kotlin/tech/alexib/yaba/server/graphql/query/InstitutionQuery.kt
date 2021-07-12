package tech.alexib.yaba.server.graphql.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.dto.InstitutionDto
import tech.alexib.yaba.server.graphql.directive.Authenticated
import tech.alexib.yaba.server.repository.InstitutionRepository

@Component
class InstitutionQuery(
    private val institutionRepository: InstitutionRepository
) : Query {


    @Authenticated
    suspend fun institutionById(institutionId: String): InstitutionDto? =
        institutionRepository.findByPlaidInstitutionId(institutionId)?.toDto()

//    @Authenticated
//    suspend fun setLogos(): Boolean {
//        institutionRepository.setLogos()
//        return true
//    }
}

