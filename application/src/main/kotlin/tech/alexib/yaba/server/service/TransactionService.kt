package tech.alexib.yaba.server.service

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.PlaidItemId
import tech.alexib.yaba.domain.user.UserId
import tech.alexib.yaba.domain.user.userId
import tech.alexib.yaba.server.fcm.FCMService
import tech.alexib.yaba.server.feature.account.AccountInsertRequest
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.account.accountId
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import tech.alexib.yaba.server.feature.transaction.TransactionTableEntity
import tech.alexib.yaba.server.feature.transaction.TransactionUpdatesEntity
import tech.alexib.yaba.server.feature.transaction.TransactionUpdatesRepository
import tech.alexib.yaba.server.feature.transaction.TransactionsUpdatedDto
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.util.notFound
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
class TransactionService(
    private val plaidService: PlaidService,
    private val transactionRepository: TransactionRepository,
    private val itemRepository: ItemRepository,
    private val accountRepository: AccountRepository,
    private val fcmService: FCMService,
    private val transactionUpdatesRepository: TransactionUpdatesRepository
) {

    suspend fun updateTransactions(
        plaidItemId: PlaidItemId,
        startDate: LocalDate,
        endDate: LocalDate,
        notifyUser: Boolean = false
    ) {
        val (accessToken, itemId) = itemRepository.findByPlaidId(plaidItemId.value).fold({
            notFound("No token for itemId $plaidItemId")
        }, {
            Pair(it.accessToken, it.id)
        })
        val accountsNotHidden =
            accountRepository.findByItemId(ItemId(itemId)).toList().filter { !it.hidden }.map { it.plaidAccountId }

        if (accountsNotHidden.isNotEmpty()) {
            handleUpdate(accessToken, startDate, endDate, plaidItemId.value, accountsNotHidden, notifyUser)
        }
    }

    private suspend fun handleUpdate(
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
        plaidItemId: String,
        accountsNotHidden: List<String>,
        notifyUser: Boolean = false
    ) {
        val result = plaidService.fetchTransactions(accessToken, startDate, endDate, accountsNotHidden)
        val accounts = result.accounts.map { AccountInsertRequest(it) }
        accountRepository.createAccounts(plaidItemId, accounts).toList()
        val exitingTransactions = transactionRepository.findInRange(
            plaidItemId,
            startDate,
            endDate
        ).map { it.plaidTransactionId }.toList()

        val newTransactions = result.transactions.filter { it.transactionId !in exitingTransactions }
            .map { transaction ->
                TransactionTableEntity.fromPlaid(
                    transaction,
                    accountRepository.findByPlaidAccountId(plaidAccountId = transaction.accountId).fold({
                        throw IllegalArgumentException("")
                    }, { it.id!!.accountId() })
                )
            }
        val incomingIds = result.transactions.map { it.transactionId }
        val transactionsToRemove: List<String> = exitingTransactions.filter { it !in incomingIds }

        val transactionsToRemoveIds = transactionRepository.findByPlaidIds(transactionsToRemove).toList()
        transactionRepository.create(newTransactions).toList().also { created ->
            if (transactionsToRemove.isNotEmpty()) {
                transactionRepository.deleteTransactions(transactionsToRemove)
            }
            insertTransactionUpdate(
                PlaidItemId(plaidItemId),
                removed = transactionsToRemoveIds,
                added = created.mapNotNull { it.id })
        }
    }

    suspend fun updateTransactions(itemId: ItemId, startDate: LocalDate, endDate: LocalDate) {
        val (accessToken, plaidItemId) = itemRepository.findById(itemId).fold({
            notFound("No token for itemId ${itemId.value}")
        }, {
            Pair(it.accessToken, it.plaidItemId)
        })
        val accountsNotHidden =
            accountRepository.findByItemId(itemId).toList().filter { !it.hidden }.map { it.plaidAccountId }
        if (accountsNotHidden.isNotEmpty()) {
            handleUpdate(accessToken, startDate, endDate, plaidItemId, accountsNotHidden)
        }
    }

    suspend fun initial(plaidItemId: PlaidItemId) {
        val startDate = LocalDate.now().minusDays(180)
        val endDate = LocalDate.now()

        updateTransactions(plaidItemId, startDate, endDate)
    }

    suspend fun initial(itemId: ItemId) {
        val startDate = LocalDate.now().minusDays(30)
        val endDate = LocalDate.now()
        updateTransactions(itemId, startDate, endDate)
    }

    suspend fun deleteTransactions(transactionIds: List<String>, plaidItemId: PlaidItemId) {
        if (transactionIds.isNotEmpty()) {
            val ids = transactionRepository.findByPlaidIds(transactionIds).toList()
            transactionRepository.deleteTransactions(transactionIds)
            insertTransactionUpdate(plaidItemId, removed = ids, added = null)
        }
    }

    suspend fun updateTransactionsOnDevice(
        userId: UserId,
        updateId: UUID
    ) {
        fcmService.sendTransactionsToUpdate(userId, updateId)
    }

    suspend fun insertTransactionUpdate(plaidItemId: PlaidItemId, added: List<UUID>?, removed: List<UUID>?) {
        itemRepository.findByPlaidId(plaidItemId.value).orNull()?.userId?.userId()?.let { userId ->
            val update = TransactionUpdatesEntity(
                userId.value,
                added = added?.map { it.toString() },
                removed = removed?.map { it.toString() }
            )
            transactionUpdatesRepository.insert(update)
            updateTransactionsOnDevice(userId, update.id)
        }
    }

    suspend fun notifyUserOfNewTransaction(
        userId: UserId,
        transaction: TransactionTableEntity
    ) {
        val name = transaction.merchantName ?: transaction.name
        val message = "$name - $${DecimalFormat("#,###.00").format(transaction.amount)}"
        logger.debug { "sending notification $message" }
        fcmService.sendNewTransactionNotification(userId, message)
    }

    suspend fun getTransactionUpdate(userId: UserId, updateId: UUID): TransactionsUpdatedDto? {
        return transactionUpdatesRepository.getById(updateId, userId).firstOrNull()?.let { update ->
            val addedIds = update.added?.map { id -> UUID.fromString(id) }
            val removedIds = update.removed?.map { id -> UUID.fromString(id) }

            val addedTransactions =
                addedIds?.let { transactionRepository.findById(it).map { transaction -> transaction.toDto() } }
            TransactionsUpdatedDto(
                added = addedTransactions,
                removed = removedIds
            )
        }
    }
}

