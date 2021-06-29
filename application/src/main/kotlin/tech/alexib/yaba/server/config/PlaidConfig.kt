package tech.alexib.yaba.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("plaid")
@ConstructorBinding
data class PlaidConfig(
    var clientId: String = "",
    var secret: String = "",
    var baseUrl: String = "",
    var hookUrl:String = ""
)
