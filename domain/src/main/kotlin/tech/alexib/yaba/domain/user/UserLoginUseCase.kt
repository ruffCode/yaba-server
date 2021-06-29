package tech.alexib.yaba.domain.user

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right

sealed class UserLoginError {
    object NotFound : UserLoginError()
    object InvalidCredentials : UserLoginError()
}

suspend inline fun LoginRequest.login(
    getUserByEmail: GetUserByEmail,
    generateToken: GenerateToken,
    passwordsMatch: PasswordMatches
): Either<UserLoginError, User> {
    val email = Email(this.email)
    val plainPassword = this.password

    return either {
        (getUserByEmail(email)?.let {
            if (passwordsMatch(plainPassword, it.password)) {
                it.copy(token = generateToken(it.id, email)).right()
            } else UserLoginError.InvalidCredentials.left()
        } ?: UserLoginError.NotFound.left()).bind()
    }

}
