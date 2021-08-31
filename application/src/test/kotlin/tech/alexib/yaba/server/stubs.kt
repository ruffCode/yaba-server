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
