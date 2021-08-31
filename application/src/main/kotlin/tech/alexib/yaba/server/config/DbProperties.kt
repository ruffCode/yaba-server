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

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions

@Configuration
@ConfigurationProperties("db")
@ConstructorBinding
data class DbProperties(
    var username: String = "",
    var password: String = "",
    var url: String = ""
)

@Configuration
@Import(*[CustomConverters::class])
class AppConfig(

    private val converters: List<Converter<*, *>?>,
) {

//    fun codecs(){
//        PostgresqlConnectionConfiguration.builder().codecRegistrar{ _, allocator, registry ->
//
//        }
//    }

    @Bean
    fun r2dbcCustomConversions(): R2dbcCustomConversions = R2dbcCustomConversions(converters)
}
