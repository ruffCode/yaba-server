package tech.alexib.yaba.server.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.json
import tech.alexib.yaba.domain.common.jSerializer

suspend inline fun <reified T> deserialize(request: ServerRequest): T =
    jSerializer.decodeFromString(request.awaitBody())

suspend inline fun <reified T> respondOk(body: T): ServerResponse =
    ServerResponse.ok().json().bodyValueAndAwait(
        jSerializer.encodeToString(body)
    )
