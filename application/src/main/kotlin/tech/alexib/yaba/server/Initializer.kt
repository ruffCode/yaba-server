package tech.alexib.yaba.server

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.fcm.FCMService
import java.util.UUID

@Component
class Initializer(
    private val fcmService: FCMService
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {

//        runBlocking {
//            fcmService.sendTransactionsToUpdate(
//                userId = UUID.fromString("f7c03741-825b-42a9-a2cd-9276140f6271").userId(),
//                updateId = UUID.fromString("368d2b88-9ac2-493e-84f0-a7e224304088")
//            )
//        }

    }
}
