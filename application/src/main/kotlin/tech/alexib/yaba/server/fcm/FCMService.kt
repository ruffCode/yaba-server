package tech.alexib.yaba.server.fcm

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.user.UserId

import tech.alexib.yaba.server.repository.PushTokenRepository

private val logger = KotlinLogging.logger {}


@Service
class FCMService(
    private val firebaseApp: FirebaseApp,
    private val pushTokenRepository: PushTokenRepository
) {

    suspend fun notifyNewTransaction(userId: UserId){
        pushTokenRepository.getUserTokens(userId).collect {
            sendNotification(it,"New transaction","QUICK CHECK")
        }

    }

    private suspend fun sendNotification(token: String, title: String, message: String) {
        val fcmMessage = Message.builder()
            .setToken(token)
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setDirectBootOk(true).build()
            )
            .setNotification(Notification.builder().setTitle(title).setBody(message).build()).build()

        try {
            FirebaseMessaging.getInstance(firebaseApp).send(fcmMessage)
            logger.info { "message sent $message" }
        } catch (e: FirebaseMessagingException) {

            logger.error {
                "FCM send error ${e.localizedMessage} Error Code ${e.errorCode}"
            }.also {
                if (e.errorCode.name == "NOT_FOUND") {
                    logger.error { "delete token" }
                    pushTokenRepository.deleteToken(token)
                }

            }
        }
    }
}
