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
