package tech.alexib.yaba.server.controller

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import tech.alexib.yaba.server.repository.InstitutionRepository
import tech.alexib.yaba.server.repository.defaultLogo
import java.util.Base64

private val logger = KotlinLogging.logger {}

@Configuration
class LogoController(
    private val logosHandler: LogosHandler
) {
    @Bean
    fun logoRouter() = coRouter {
        GET("logo/{filename}", logosHandler::sendLogo)
    }
}

@Component
class LogosHandler(
    private val repository: InstitutionRepository
) {

    suspend fun sendLogo(serverRequest: ServerRequest): ServerResponse {
        val fileName = serverRequest.pathVariable("filename")
        if (!fileName.contains("png")) return ServerResponse.badRequest().buildAndAwait()

        val logo = repository.findLogo(fileName.substringBefore(".")).firstOrNull() ?: defaultLogo

        val logoBytes = Base64.getDecoder().decode(logo)

        return ServerResponse.ok().contentType(MediaType.IMAGE_PNG).bodyValue(logoBytes).awaitSingle()

    }
}


//private fun base64ToBitmap(b64: String): Bitmap {
//    val imageAsBytes: ByteArray = Base64.getDecoder().decode(b64.toByteArray())
//    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
//}
