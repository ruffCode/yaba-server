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
package tech.alexib.yaba.server.graphql.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class BeanUtil : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    @Suppress("unused")
    companion object {
        lateinit var context: ApplicationContext
            private set

        fun <T : Any> getBean(type: KClass<T>): T {
            return context.getBean(type.java)
        }

        inline fun <reified T> getBean(): T {
            return context.getBean(T::class.java)
        }

        fun <T> getBean(type: Class<T>): T {
            return context.getBean(type)
        }

        fun <T> getBean(name: String, beanClass: Class<T>): T {
            return context.getBean(name, beanClass)
        }

        fun <T> getBeanOrNull(type: Class<T>): T? {
            val beanNames = context.getBeanNamesForType(type)
            return if (beanNames.size != 1) {
                null
            } else context.getBean(beanNames[0], type)
        }
    }
}
