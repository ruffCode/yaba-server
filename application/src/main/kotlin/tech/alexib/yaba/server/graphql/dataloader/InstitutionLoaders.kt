package tech.alexib.yaba.server.graphql.dataloader

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.dto.InstitutionDto
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.graphql.util.CoroutineDataLoader
import tech.alexib.yaba.server.graphql.util.getValueFromDataLoader
import tech.alexib.yaba.server.repository.InstitutionRepository
import java.util.UUID
import java.util.concurrent.CompletableFuture
private val logger = KotlinLogging.logger {}
@Component
class InstitutionDataLoader(
    private val institutionRepository: InstitutionRepository
) : CoroutineDataLoader<UUID, InstitutionDto>() {
    override suspend fun batchLoad(keys: List<UUID>): List<InstitutionDto> {
        logger.info { "InstitutionDataLoader called with $keys" }
        return institutionRepository.findById(keys).map { it.toDto() }
    }
}

@Component
class InstitutionByPlaidInstitutionIdDataFetcher : DataFetcher<CompletableFuture<InstitutionDto>> {
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<InstitutionDto> {
        val id = environment.getSource<ItemDto>().plaidInstitutionId
        return environment.getValueFromDataLoader(InstitutionByPlaidInstitutionIdLoader::class, id)
    }
}
@Component
class InstitutionByPlaidInstitutionIdLoader(
    private val institutionRepository: InstitutionRepository
) : CoroutineDataLoader<String, InstitutionDto>() {
    override suspend fun batchLoad(keys: List<String>): List<InstitutionDto> {
        return institutionRepository.findByPlaidInstitutionId(keys).map { it.toDto() }
    }
}
