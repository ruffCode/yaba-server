package tech.alexib.yaba.server

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import graphql.GraphQLError
import graphql.GraphqlErrorException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import tech.alexib.yaba.domain.common.jSerializer
import tech.alexib.yaba.server.config.ServiceAccountPath
import tech.alexib.yaba.server.graphql.MySubscriptionHooks
import tech.alexib.yaba.server.graphql.dataloader.CustomKotlinDataFetcherFactoryProvider
import tech.alexib.yaba.server.graphql.schema.CustomDirectiveWiringFactory

import tech.alexib.yaba.server.graphql.schema.CustomSchemaGeneratorHooks
import tech.alexib.yaba.server.util.YabaException

//@Configuration
//class SerializerConfig :WebFluxConfigurer{
//
//    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
//        super.configureHttpMessageCodecs(configurer)
//    }
//
//    @Bean
//    fun converter(): KotlinSerializationJsonDecoder = KotlinSerializationJsonDecoder(jSerializer)
//}

@SpringBootApplication
class YabaServerApplication {
    @Bean
    fun additionalConverters(): HttpMessageConverters {
        return HttpMessageConverters(KotlinSerializationJsonHttpMessageConverter(jSerializer))
    }
    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(wiringFactory: KotlinDirectiveWiringFactory) = CustomSchemaGeneratorHooks(wiringFactory)

    @Bean
    fun kotlinDataFetcherFactoryProvider(objectMapper: ObjectMapper) =
        CustomKotlinDataFetcherFactoryProvider(objectMapper)

    @Bean
    fun apolloSubscriptionHooks(): ApolloSubscriptionHooks = MySubscriptionHooks()


}

@Configuration
class FirebaseConfig(
    private val serviceAccountPath: ServiceAccountPath
) {
    @Bean
    fun initFirebase(): FirebaseApp {
        val resource = ClassPathResource(serviceAccountPath.path)
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else FirebaseApp.getInstance()
    }
}

fun main(args: Array<String>) {
    runApplication<YabaServerApplication>(*args)
}

private val logger = KotlinLogging.logger {}

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {


    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = when (exception) {
            is YabaException -> GraphqlErrorException.newErrorException()
                .cause(exception)
                .message(exception.message ?: "UNKNOWN ERROR")
                .sourceLocation(sourceLocation)
                .path(path.toList())
                .build()
//            is ValidationException -> ValidationDataFetchingGraphQLError(exception.constraintErrors, path, exception, sourceLocation)
            else ->
                GraphqlErrorException.newErrorException()
                    .cause(exception)
                    .message(exception.message)
                    .sourceLocation(sourceLocation)
                    .path(path.toList())
                    .build()
        }

//        val error = GraphqlErrorException.newErrorException()
//            .cause(exception)
//            .message(exception.message)
//            .sourceLocation(sourceLocation)
//            .path(path.toList())
//            .build()
        logger.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }
}
