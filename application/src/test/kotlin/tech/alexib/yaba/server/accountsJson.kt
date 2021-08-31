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

val accountsJson = """
    {
        "accounts": [
            {
                "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
                "balances": {
                    "available": 100,
                    "current": 110,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "0000",
                "name": "Plaid Checking",
                "official_name": "Plaid Gold Standard 0% Interest Checking",
                "subtype": "checking",
                "type": "depository"
            },
            {
                "account_id": "ALqLG4z3y9fzdkxbXNZNuMK9DPwxGwUWB5wLn",
                "balances": {
                    "available": 200,
                    "current": 210,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "1111",
                "name": "Plaid Saving",
                "official_name": "Plaid Silver Standard 0.1% Interest Saving",
                "subtype": "savings",
                "type": "depository"
            },
            {
                "account_id": "GWjWlR4PKkir74nwbL5LT9DR46an1aUWZo9KD",
                "balances": {
                    "available": null,
                    "current": 1000,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "2222",
                "name": "Plaid CD",
                "official_name": "Plaid Bronze Standard 0.2% Interest CD",
                "subtype": "cd",
                "type": "depository"
            },
            {
                "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
                "balances": {
                    "available": null,
                    "current": 410,
                    "iso_currency_code": "USD",
                    "limit": 2000,
                    "unofficial_currency_code": null
                },
                "mask": "3333",
                "name": "Plaid Credit Card",
                "official_name": "Plaid Diamond 12.5% APR Interest Credit Card",
                "subtype": "credit card",
                "type": "credit"
            },
            {
                "account_id": "bL8LAwKVPjf5PNdzjK4KtE3dR6ZmkZuGB7LKW",
                "balances": {
                    "available": 43200,
                    "current": 43200,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "4444",
                "name": "Plaid Money Market",
                "official_name": "Plaid Platinum Standard 1.85% Interest Money Market",
                "subtype": "money market",
                "type": "depository"
            },
            {
                "account_id": "Qr9r47LoPWfjWRBkZL5LU1DEpmorkoTkjDo69",
                "balances": {
                    "available": null,
                    "current": 320.76,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "5555",
                "name": "Plaid IRA",
                "official_name": null,
                "subtype": "ira",
                "type": "investment"
            },
            {
                "account_id": "ZPdP1pnVzjFob6lgWdKdC3pleGLPDLi8xGLm4",
                "balances": {
                    "available": null,
                    "current": 23631.9805,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "6666",
                "name": "Plaid 401k",
                "official_name": null,
                "subtype": "401k",
                "type": "investment"
            },
            {
                "account_id": "MyEy3RQMdZfaLQVKoA5ACL8bXGxVvxcDm1xpE",
                "balances": {
                    "available": null,
                    "current": 65262,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "7777",
                "name": "Plaid Student Loan",
                "official_name": null,
                "subtype": "student",
                "type": "loan"
            },
            {
                "account_id": "1zPzkLgp7wuaDE5VmRqRC8glVZjvyjtQDr7XG",
                "balances": {
                    "available": null,
                    "current": 56302.06,
                    "iso_currency_code": "USD",
                    "limit": null,
                    "unofficial_currency_code": null
                },
                "mask": "8888",
                "name": "Plaid Mortgage",
                "official_name": null,
                "subtype": "mortgage",
                "type": "loan"
            }
        ]
    }
""".trimIndent()
