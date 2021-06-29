package tech.alexib.yaba.server.config

import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

private val logger = KotlinLogging.logger { }
@Profile("!test")
@Configuration
class FlywayConfig(
    private val env: Environment,
//    private val dbProperties: DbProperties
) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        logger.info {
            """
                flyway
                ${env.getRequiredProperty("db.username")}
            """.trimIndent()
        }
        val url = "jdbc:" + env.getRequiredProperty("db.url")
        val user = env.getRequiredProperty("db.username")
        val password = env.getRequiredProperty("db.password")
        val config = Flyway
            .configure()
            .dataSource(url, user, password)
        return Flyway(config)
    }
}
