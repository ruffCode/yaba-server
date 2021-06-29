package tech.alexib.yaba.server

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.repository.InstitutionRepository

@Component
class Initializer(
    private val plaidService: PlaidService,
    private val institutionRepository: InstitutionRepository
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {

//        runBlocking {
//            val all = institutionRepository.findAll()
//           if(all.toList().isEmpty()){
//               plaidService.institutions(500, 2000).institutions.map { it.toEntity() }.also {
//                   institutionRepository.insert(it).toList()
//               }
//           }
//        }
    }
}
