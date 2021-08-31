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
package tech.alexib.yaba.server.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
private val logger = KotlinLogging.logger {}

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
            logger.error { "Password encode error ${e.localizedMessage}" }
            throw e
        }
    }

    override fun matches(cs: CharSequence?, pw: String?): Boolean {
        return encode(cs) == pw
    }
}
