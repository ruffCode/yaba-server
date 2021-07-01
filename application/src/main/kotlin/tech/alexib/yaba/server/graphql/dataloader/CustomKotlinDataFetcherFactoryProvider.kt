package tech.alexib.yaba.server.graphql.dataloader

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.getBean
import tech.alexib.yaba.server.feature.account.AccountDto
import tech.alexib.yaba.server.feature.account.AccountsByItemIdDataFetcher
import tech.alexib.yaba.server.feature.account.AccountsByUserIdDataFetcher
import tech.alexib.yaba.server.feature.item.ItemDto
import tech.alexib.yaba.server.feature.item.ItemsByItemIdDataFetcher
import tech.alexib.yaba.server.feature.item.ItemsByUserIdDataFetcher
import tech.alexib.yaba.server.feature.transaction.TransactionByItemIdDataFetcher
import tech.alexib.yaba.server.feature.transaction.TransactionsByAccountIdDataFetcher
import tech.alexib.yaba.server.feature.transaction.TransactionsByUserIdDataFetcher
import tech.alexib.yaba.server.feature.user.UserDto
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class CustomKotlinDataFetcherFactoryProvider(
    objectMapper: ObjectMapper
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper), BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> {

        @Suppress("UNCHECKED_CAST")
        return when (kProperty) {
            UserDto::items -> DataFetcherFactory {
                beanFactory.getBean<ItemsByUserIdDataFetcher>() as DataFetcher<Any>
            }
            UserDto::accounts -> DataFetcherFactory {
                beanFactory.getBean<AccountsByUserIdDataFetcher>() as DataFetcher<Any>
            }
            UserDto::transactions -> DataFetcherFactory {
                beanFactory.getBean<TransactionsByUserIdDataFetcher>() as DataFetcher<Any>
            }
            ItemDto::institution -> DataFetcherFactory {
                beanFactory.getBean<InstitutionByPlaidInstitutionIdDataFetcher>() as DataFetcher<Any>
            }
            ItemDto::transactions -> DataFetcherFactory {
                beanFactory.getBean<TransactionByItemIdDataFetcher>() as DataFetcher<Any>
            }
            ItemDto::accounts -> DataFetcherFactory {
                beanFactory.getBean<AccountsByItemIdDataFetcher>() as DataFetcher<Any>
            }
            AccountDto::transactions -> DataFetcherFactory {
                beanFactory.getBean<TransactionsByAccountIdDataFetcher>() as DataFetcher<Any>
            }
            AccountDto::item -> DataFetcherFactory {
                beanFactory.getBean<ItemsByItemIdDataFetcher>() as DataFetcher<Any>
            }
            else -> super.propertyDataFetcherFactory(kClass, kProperty)
        }
    }
}
