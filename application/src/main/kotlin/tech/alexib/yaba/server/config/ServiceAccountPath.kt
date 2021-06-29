package tech.alexib.yaba.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("service-account")
data class ServiceAccountPath(
    var path:String = ""
)
