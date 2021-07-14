package tech.alexib.yaba.domain.common

import tech.alexib.yaba.domain.user.Email
import tech.alexib.yaba.domain.user.User
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.UserRole

interface AuthUtil {
    fun encodePassword(password: String): String
    fun generateToken(userId: UserId,email: Email): String
    fun passwordsMatch(plainPassword: String, encodedPassword: String): Boolean
}
