package tech.alexib.yaba.domain.user

import arrow.core.Either

fun interface ValidateUserRegistration {
    suspend operator fun invoke(req: RegisterUserRequest): Either<UserRegistrationError, ValidUserRegistration>
}

fun interface CreateUser{
    suspend operator fun invoke(user: ValidUserRegistration):User
}

fun interface GetUserByEmail{
    suspend operator fun invoke(email: Email):User?
}

fun interface ExistsByEmail{
    suspend operator fun invoke(email: Email):Boolean
}
fun interface EncodePassword{
    operator fun invoke(password: String):String
}
fun interface GenerateToken{
    operator fun invoke(userId: UserId,email: Email):String
}

fun interface PasswordMatches{
    operator fun invoke(password: String,encodedPassword:String):Boolean
}
