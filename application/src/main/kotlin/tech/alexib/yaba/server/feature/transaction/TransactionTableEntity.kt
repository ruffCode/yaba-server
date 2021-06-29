package tech.alexib.yaba.server.feature.transaction

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import tech.alexib.plaid.client.model.Transaction
import tech.alexib.yaba.server.feature.account.AccountId
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Table("transactions_table")
data class TransactionTableEntity(
    @Column("account_id")
    val accountId: UUID,
    @Column("plaid_transaction_id")
    val plaidTransactionId: String,
    @Column("plaid_category_id")
    val plaidCategoryId: String? = null,
    val category: String? = null,
    val subcategory: String? = null,
    val type: String,
    val name: String,
    val amount: Double,
    @Column("iso_currency_code")
    val isoCurrencyCode: String? = "USD",
    @Column("unofficial_currency_code")
    val unofficialCurrencyCode: String? = "",
    val date: LocalDate,
    val pending: Boolean,
    @Column("account_owner")
    val accountOwner: String,
    @Column("created_at")
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    @Id
    val id: UUID? = null,
) {
    companion object {
        fun fromPlaid(transaction: Transaction, accountId: AccountId) = TransactionTableEntity(
            accountId = accountId.value,
            plaidTransactionId = transaction.transactionId,
            plaidCategoryId = transaction.categoryId,

            category = transaction.category?.firstOrNull(),
            subcategory = transaction.category?.drop(1)?.firstOrNull(),
            type = transaction.transactionType?.name ?: "",
            name = transaction.name ?: "",
            amount = transaction.amount,
            isoCurrencyCode = transaction.isoCurrencyCode,
            unofficialCurrencyCode = transaction.unofficialCurrencyCode,
            date = LocalDate.parse(transaction.date),
            pending = transaction.pending,
            accountOwner = transaction.accountOwner ?: "",
        )
    }
}
