package tech.alexib.yaba.server.feature.transaction


import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Table("transactions")
data class TransactionEntity(
    @Column("account_id")
    val accountId: UUID,
    @Column("plaid_transaction_id")
    val plaidTransactionId: String,
    @Column("plaid_account_id")
    val plaidAccountId: String,
    @Column("plaid_item_id")
    val plaidItemId: String,
    @Column("item_id")
    val itemId: UUID,
    val category: String? = null,
    val subcategory: String? = null,
    val type: String,
    val name: String,
    val amount: Double,
    @Column("user_id")
    val userId: UUID,
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
    @Column("merchant_name")
    val merchantName: String? = null,
) {
    fun toDto() = TransactionDto(
        id = id!!,
        accountId = accountId,
        itemId = itemId,
        category = category,
        subcategory = subcategory,
        type = type,
        name = name,
        amount = amount,
        userId = userId,
        isoCurrencyCode = isoCurrencyCode,
        unofficialCurrencyCode = unofficialCurrencyCode,
        date = date,
        pending = pending,
        merchantName = merchantName
    )
}


