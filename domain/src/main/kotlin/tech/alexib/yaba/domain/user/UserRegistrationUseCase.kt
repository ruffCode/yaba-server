package tech.alexib.yaba.domain.user

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import java.util.UUID

sealed class UserRegistrationError {
    object DuplicateEmail : UserRegistrationError()
    object InvalidEmail : UserRegistrationError()
    object PasswordTooShort : UserRegistrationError()
}

suspend inline fun RegisterUserRequest.validate(
    existsByEmail: ExistsByEmail,
    encodePassword: EncodePassword,
    generateToken: GenerateToken
): Either<UserRegistrationError, ValidUserRegistration> {
    val cmd = this
    return either {
        when {
            !cmd.email.isValid() -> UserRegistrationError.InvalidEmail.left()
            existsByEmail(cmd.email) -> UserRegistrationError.DuplicateEmail.left()
            cmd.password.length < 12 -> UserRegistrationError.PasswordTooShort.left()
            else -> {
                val id = UUID.randomUUID().userId()
                ValidUserRegistration(
                    id,
                    email = cmd.email,
                    password = encodePassword(cmd.password),
                    token = generateToken(id, email)
                ).right()
            }
        }.bind()
    }
}

suspend inline fun RegisterUserCommand.register(
    createUser: CreateUser,
    validateUser: ValidateUserRegistration
): Either<UserRegistrationError, User> {
    val cmd = this
    return either {
        val validUser = validateUser(cmd.data).bind()
        createUser(validUser)
    }
}

