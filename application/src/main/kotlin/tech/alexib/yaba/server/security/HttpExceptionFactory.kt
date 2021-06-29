package tech.alexib.yaba.server.security

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object HttpExceptionFactory {
    fun badRequest(): ResponseStatusException = ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")

    fun unauthorized(): ResponseStatusException = ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
}
