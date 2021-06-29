package tech.alexib.yaba.server.config

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer

private val logger = KotlinLogging.logger { }

@Configuration
class IntegrationTestConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun postgresqlContainer(): PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
//        withInitScript(INIT_SCRIPT)
        withUsername(USERNAME)
        withPassword(PASSWORD)
        withDatabaseName(DB_NAME)
        withUrlParam("TC_IMAGE_TAG", "13.1")

        withClasspathResourceMapping("db/init.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY)
    }

    @Bean
    @Primary
    fun connectionFactory(pgContainer: PostgreSQLContainer<Nothing>) = PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host("127.0.0.1")
            .port(pgContainer.getMappedPort(5432))
            .database(pgContainer.databaseName)
            .username(pgContainer.username)
            .password(pgContainer.password)
            .build()
    )

//    @Bean(initMethod = "migrate")
//    @Primary
//    fun flyway(pgContainer: PostgreSQLContainer<Nothing>): Flyway {
//
//        val url = "jdbc:" + "tc:postgresql://localhost:5432/yaba"
//        val user = "yaba"
//        val password = "yaba"
//        val config = Flyway
//            .configure()
//            .dataSource(url, user, password)
//        return Flyway(config)
//    }

    companion object {
        private const val DB_NAME = "yaba"
        private const val USERNAME = "yaba"
        private const val PASSWORD = "yaba"
        private const val INIT_SCRIPT = "db/init.sql"
    }
}

