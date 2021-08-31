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

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

private val logger = KotlinLogging.logger { }

@Configuration
class IntegrationTestConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun postgresqlContainer(): PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
        withUsername(USERNAME)
        withPassword(PASSWORD)
        withDatabaseName(DB_NAME)
        withUrlParam("TC_IMAGE_TAG", "13.1")
//        withClasspathResourceMapping("db/init.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY)
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
    ).also { flyway(pgContainer) }

    fun flyway(pgContainer: PostgreSQLContainer<Nothing>) {
        with(pgContainer) {
            waitingFor(Wait.forHealthcheck())
            val url = "jdbc:postgresql://${pgContainer.host}:${pgContainer.firstMappedPort}/yaba"
            FluentConfiguration().dataSource(url, "yaba", "yaba")
                .locations("classpath:/db/migration")
                .apply { Flyway(this).migrate() }
        }
    }

    companion object {
        private const val DB_NAME = "yaba"
        private const val USERNAME = "yaba"
        private const val PASSWORD = "yaba"
    }
}
