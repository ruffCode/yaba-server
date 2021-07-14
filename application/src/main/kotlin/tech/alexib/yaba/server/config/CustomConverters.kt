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
class CustomConverters(

) {
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
    class UserRoleWritingConverter:EnumWriteSupport<UserRole>()

}
