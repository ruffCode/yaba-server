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
package tech.alexib.yaba.server

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import graphql.execution.DataFetcherExceptionHandler
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

@SpringBootApplication
class YabaServerApplication {
    @Bean
    fun additionalConverters(): HttpMessageConverters {
        return HttpMessageConverters(KotlinSerializationJsonHttpMessageConverter(jSerializer))
    }

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun wiringFactory(): KotlinDirectiveWiringFactory = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(wiringFactory: KotlinDirectiveWiringFactory):
        SchemaGeneratorHooks = CustomSchemaGeneratorHooks(wiringFactory)

    @Bean
    fun kotlinDataFetcherFactoryProvider(objectMapper: ObjectMapper): SimpleKotlinDataFetcherFactoryProvider =
        CustomKotlinDataFetcherFactoryProvider(objectMapper)

    @Bean
    fun apolloSubscriptionHooks(): ApolloSubscriptionHooks = MySubscriptionHooks()
}

@Configuration
class FirebaseConfig(
    private val serviceAccountPath: ServiceAccountPath,
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
