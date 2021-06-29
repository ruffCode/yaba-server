package tech.alexib.yaba.server.util

import io.r2dbc.spi.Row
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Locale
import kotlin.reflect.KProperty

inline fun <reified T> Row.getProp(propertyName:String): T =
    get(propertyName, T::class.java) as T

inline fun <reified T> Row.get(property: KProperty<T>): T =
    get(property.name.camelToSnakeCase(), T::class.java) as T

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.lowercase()
}

inline fun <reified T> R2dbcConverter.read(row: Row, prefix: String): T? =
    read(T::class.java,
        object : Row {
            override fun <T : Any?> get(index: Int, type: Class<T>): T? = row[index, type]
            override fun <T : Any?> get(name: String, type: Class<T>): T? = row["$prefix$name", type]
        }
    )
fun <T> withTransaction(transactionManager: R2dbcTransactionManager, block: suspend CoroutineScope.() -> T?): Mono<T> {
    val operator = TransactionalOperator.create(transactionManager)
    val result = mono(block = block)
    return operator.transactional(result)
}


fun <T : Any> withTransaction(transactionManager: R2dbcTransactionManager, flow: Flow<T>): Flux<T> {
    val operator = TransactionalOperator.create(transactionManager)
    val result = flow.asFlux()
    return operator.transactional(result)
}


suspend fun <T> withTransactionOnCoroutine(
    transactionManager: R2dbcTransactionManager,
    block: suspend CoroutineScope.() -> T?
): T = withTransaction(transactionManager, block).awaitSingle()


suspend fun <T : Any> withTransactionOnCoroutine(transactionManager: R2dbcTransactionManager, flow: Flow<T>): Flow<T> =
    withTransaction(transactionManager, flow).asFlow()
