package tech.alexib.yaba.server.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Base64
import org.springframework.security.crypto.password.PasswordEncoder
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Component
class PasswordEncoder : PasswordEncoder {
    @Autowired
    lateinit var jwtConfig: JwtConfig
    val iteration: Int = 33
    val keyLength: Int = 256

    override fun encode(cs: CharSequence?): String {
        return try {
            val result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                .generateSecret(
                    PBEKeySpec(
                        cs.toString().toCharArray(),
                        jwtConfig.secret.toByteArray(),
                        iteration,
                        keyLength
                    )
                )
                .encoded
            Base64.getEncoder().encodeToString(result)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun matches(cs: CharSequence?, pw: String?): Boolean {
        return encode(cs) == pw
    }

}
