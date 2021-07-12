package tech.alexib.yaba.server.fcm

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.server.repository.PushTokenRepository
import java.util.UUID

private val logger = KotlinLogging.logger {}

interface FCMService {
    suspend fun sendNewTransactionNotification(userId: UserId, message: String)
    suspend fun sendTransactionsToUpdate(userId: UserId, updateId: UUID)
}

@Profile("test")
@Service
class FCMServiceFake : FCMService {

    override suspend fun sendNewTransactionNotification(userId: UserId, message: String) {
        logger.debug { "sending new transaction notification to $userId - message: $message" }
    }

    override suspend fun sendTransactionsToUpdate(userId: UserId, updateId: UUID) {
        logger.debug { "sending tranasctions to update to $userId - updateId: $updateId" }
    }
}

@Profile("!test")
@Service
class FCMServiceReal(
    private val firebaseApp: FirebaseApp,
    private val pushTokenRepository: PushTokenRepository
) : FCMService {

    override suspend fun sendNewTransactionNotification(userId: UserId, message: String) {
        val tokens = pushTokenRepository.getUserTokens(userId).toList()
        if (tokens.isNotEmpty()) {
            val fcmMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder().setChannelId("new_transaction").build())
                        .setDirectBootOk(true).build()
                ).setNotification(Notification.builder().setTitle("New transaction").setBody(message).build()).build()

            sendMessage(fcmMessage)
        }
    }

    override suspend fun sendTransactionsToUpdate(userId: UserId, updateId: UUID) {
        logger.debug {
            """
            sendTransactionsToUpdate
            $userId
           updateId $updateId
        """.trimIndent()
        }
        val tokens = pushTokenRepository.getUserTokens(userId).toList()
        if (tokens.isNotEmpty()) {
            val fcmMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putAllData(mapOf("updateId" to updateId.toString()))
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setDirectBootOk(true).build()
                ).build()
            FirebaseMessaging.getInstance(firebaseApp).sendMulticast(fcmMessage)
        }
    }


    private fun sendMessage(fcmMessage: MulticastMessage) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).sendMulticast(fcmMessage)
        } catch (e: FirebaseMessagingException) {
            logger.error {
                "FCM send error ${e.localizedMessage} Error Code ${e.errorCode}"
            }.also {
                if (e.errorCode.name == "NOT_FOUND") {
                    logger.error { "token not found" }
//                    pushTokenRepository.deleteToken(token)
                }
            }
        }
    }
}
