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

// private fun base64ToBitmap(b64: String): Bitmap {
//    val imageAsBytes: ByteArray = Base64.getDecoder().decode(b64.toByteArray())
//    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
// }
