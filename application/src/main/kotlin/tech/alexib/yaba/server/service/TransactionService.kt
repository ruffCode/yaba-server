package tech.alexib.yaba.server.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import tech.alexib.yaba.domain.item.ItemId
import tech.alexib.yaba.domain.item.PlaidItemId
import tech.alexib.yaba.server.plaid.PlaidService
import tech.alexib.yaba.server.feature.account.AccountInsertRequest
import tech.alexib.yaba.server.feature.transaction.TransactionTableEntity
import tech.alexib.yaba.server.feature.account.accountId
import tech.alexib.yaba.server.feature.account.AccountRepository
import tech.alexib.yaba.server.feature.item.ItemRepository
import tech.alexib.yaba.server.feature.transaction.TransactionRepository
import tech.alexib.yaba.server.util.notFound
import java.time.LocalDate

@Service
class TransactionService(
    private val plaidService: PlaidService,
    private val transactionRepository: TransactionRepository,
    private val itemRepository: ItemRepository,
    private val accountRepository: AccountRepository
) {

    suspend fun updateTransactions(plaidItemId: PlaidItemId, startDate: LocalDate, endDate: LocalDate) {
        val (accessToken, itemId) = itemRepository.findByPlaidId(plaidItemId.value).fold({
            notFound("No token for itemId $plaidItemId")
        }, {
            Pair(it.accessToken, it.id)
        })
        val accountsNotHidden =
            accountRepository.findByItemId(ItemId(itemId)).toList().filter { !it.hidden }.map { it.plaidAccountId }

        if(accountsNotHidden.isNotEmpty()){
            handleUpdate(accessToken, startDate, endDate, plaidItemId.value, accountsNotHidden)
        }
    }

    private suspend fun handleUpdate(
        accessToken: String,
        startDate: LocalDate,
        endDate: LocalDate,
        plaidItemId: String,
        accountsNotHidden: List<String>
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
        val transactionsToRemove = exitingTransactions.filter { it !in incomingIds }
        transactionRepository.create(newTransactions).toList()
        transactionRepository.deleteTransactions(transactionsToRemove)
    }

    suspend fun updateTransactions(itemId: ItemId, startDate: LocalDate, endDate: LocalDate) {
        val (accessToken, plaidItemId) = itemRepository.findById(itemId).fold({
            notFound("No token for itemId ${itemId.value}")
        }, {
            Pair(it.accessToken, it.plaidItemId)
        })
        val accountsNotHidden =
            accountRepository.findByItemId(itemId).toList().filter { !it.hidden }.map { it.plaidAccountId }
        if(accountsNotHidden.isNotEmpty()){
            handleUpdate(accessToken, startDate, endDate, plaidItemId, accountsNotHidden)
        }
    }

    suspend fun initial(plaidItemId: PlaidItemId) {
        val startDate = LocalDate.now().minusDays(180)
        val endDate = LocalDate.now()

        updateTransactions(plaidItemId, startDate, endDate)
    }

    suspend fun initial(itemId: ItemId) {
        val startDate = LocalDate.now().minusDays(180)
        val endDate = LocalDate.now()
        updateTransactions(itemId, startDate, endDate)
    }

    suspend fun deleteTransactions(transactionIds: List<String>) {
        transactionRepository.deleteTransactions(transactionIds)
    }

}
