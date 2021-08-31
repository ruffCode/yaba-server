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

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.EnumWriteSupport
import tech.alexib.yaba.domain.user.UserRole
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@Configuration
class CustomConverters {
    @Bean
    fun converters(): List<Converter<*, *>?> = listOf<Converter<*, *>?>(

        UUIDWritingConverter(),
        InstantToOffsetDateTimeConverter(),
        OffsetDateTimeToInstantConverter(),
        LocalDateToDateConverter(),
        JavaLocalDateToKtLocalDateConverter(),
        UserRoleWritingConverter()
    )

    @WritingConverter
    class UUIDWritingConverter : Converter<UUID, UUID> {
        override fun convert(source: UUID): UUID = source
    }

    @WritingConverter
    class InstantToOffsetDateTimeConverter : Converter<Instant, OffsetDateTime> {
        override fun convert(source: Instant): OffsetDateTime? =
            OffsetDateTime.ofInstant(source.toJavaInstant(), ZoneId.systemDefault())
    }

    @ReadingConverter
    class OffsetDateTimeToInstantConverter : Converter<OffsetDateTime, Instant> {
        override fun convert(source: OffsetDateTime): Instant =
            Instant.fromEpochSeconds(source.toEpochSecond())
    }

    @WritingConverter
    class LocalDateToDateConverter : Converter<LocalDate, java.time.LocalDate> {
        override fun convert(source: LocalDate): java.time.LocalDate =
            source.toJavaLocalDate()
    }

    @ReadingConverter
    class JavaLocalDateToKtLocalDateConverter : Converter<java.time.LocalDate, LocalDate> {
        override fun convert(source: java.time.LocalDate): LocalDate =
            LocalDate(source.year, source.monthValue, source.dayOfMonth)
    }

    @WritingConverter
    class UserRoleWritingConverter : EnumWriteSupport<UserRole>()
}
