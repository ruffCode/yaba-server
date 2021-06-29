package tech.alexib.yaba.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app")
@ConstructorBinding
data class JwtConfig(
    var secret: String = "",
    var refresh: String = ""
)
