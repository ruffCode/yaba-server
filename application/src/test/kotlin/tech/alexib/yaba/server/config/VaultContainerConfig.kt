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
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.vault.VaultContainer
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {}

@Configuration
class VaultContainerConfig {
    val container = VaultContainer<Nothing>("vault:1.8.1").apply {
        withVaultToken("root")
        withClasspathResourceMapping(
            "/vault/init.sh",
            "/opt/init.sh",
            BindMode.READ_ONLY
        )
    }

    fun startVault() {
        with(container) {
            start()
            waitingFor(Wait.forHealthcheck())

            val result = execInContainer(Charset.defaultCharset(), "sh", "/opt/init.sh")
            logger.info { result }
        }
    }
}
