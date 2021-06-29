package tech.alexib.yaba.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import tech.alexib.plaid.client.model.Account
import tech.alexib.plaid.client.model.Transaction
import tech.alexib.yaba.domain.common.jSerializer
import tech.alexib.yaba.domain.institution.InstitutionId
import tech.alexib.yaba.domain.item.Item
import tech.alexib.yaba.domain.user.User

object ItemStub {

    val item = Item(
        plaidInstitutionId = InstitutionId("ins_4"),
        plaidItemId = "5dLdQJAEyVu8LDBy5vEvF6QJ55zELWcxjGMxr",
        plaidAccessToken = "access-sandbox-d69b3dee-a318-4064-bcba-05d297a4816a",
        status = "good",
        userId = UsersStub.user.id
    )
}

object UsersStub {
    val users = listOf(
        User(email = "user1@gmail.com", password = "password"),
        User(email = "user2@gmail.com", password = "password"),
        User(email = "user3@gmail.com", password = "password")
    )

    val user = users.first()
}

object AccountStub {
    val plaidAccounts = jSerializer.decodeFromString(AccountsList.serializer(), accountsJson)
}

object TransactionsStub {
    val transactions: TransactionList = jSerializer.decodeFromString(transactionsJson)
}


@Serializable
data class AccountsList(val accounts: List<Account>)

@Serializable
data class TransactionList(val transactions: List<Transaction>)
