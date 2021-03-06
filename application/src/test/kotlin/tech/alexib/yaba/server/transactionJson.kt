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

val transactionsJson = """
    {"transactions":[
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 6.33,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Travel",
                "Taxi"
            ],
            "category_id": "22016000",
            "date": "2021-04-23",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Uber",
            "name": "Uber 072515 SF**POOL**",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "bL8LAwKVPjf5PNdzjKQkFllxekmoL9iGyAg17",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 500,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Food and Drink",
                "Restaurants"
            ],
            "category_id": "13005000",
            "date": "2021-04-20",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Tectra Inc",
            "name": "Tectra Inc",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "Kbpbzy7Q3VfxMgrdeDqRSWXbxbgXMkI6rxla3",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 2078.5,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Payment"
            ],
            "category_id": "16000000",
            "date": "2021-04-19",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": null,
            "name": "AUTOMATIC PAYMENT - THANK",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "WaKaQpWGq4i1LVZ7qo6ncZj7l8rwZAiWeAvkv",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 500,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Food and Drink",
                "Restaurants",
                "Fast Food"
            ],
            "category_id": "13005032",
            "date": "2021-04-19",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "KFC",
            "name": "KFC",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "E8a8ynd4lrfqP1BvQDGEfNA1EopgNBujWvVqE",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 500,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Shops",
                "Sporting Goods"
            ],
            "category_id": "19046000",
            "date": "2021-04-19",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Madison Bicycle Shop",
            "name": "Madison Bicycle Shop",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "8NWN1vbGyRtWX17EoKrBuBV9pzmKBJCxD4zbK",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "ALqLG4z3y9fzdkxbXNZNuMK9DPwxGwUWB5wLn",
            "account_owner": null,
            "amount": 25,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Payment",
                "Credit Card"
            ],
            "category_id": "16001000",
            "date": "2021-04-10",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": null,
            "name": "CREDIT CARD 3333 PAYMENT *//",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "PKmKaj1pNEtPgW6AZD54ikgRWBnz6pUwNJRoW",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 5.4,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Travel",
                "Taxi"
            ],
            "category_id": "22016000",
            "date": "2021-04-10",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Uber",
            "name": "Uber 063015 SF**POOL**",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "xaEaeR8VkXi6e8bgl4Prt4qJE7zlo6i9ANb6x",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "bL8LAwKVPjf5PNdzjK4KtE3dR6ZmkZuGB7LKW",
            "account_owner": null,
            "amount": 5850,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Transfer",
                "Debit"
            ],
            "category_id": "21006000",
            "date": "2021-04-09",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": null,
            "name": "ACH Electronic CreditGUSTO PAY 123456",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": "ACH",
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "9PGPL5mnywFzlbxD9raxfLbxPeWrGyulJpNxN",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "GWjWlR4PKkir74nwbL5LT9DR46an1aUWZo9KD",
            "account_owner": null,
            "amount": 1000,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Transfer",
                "Deposit"
            ],
            "category_id": "21007000",
            "date": "2021-04-09",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": null,
            "name": "CD DEPOSIT .INITIAL.",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "mmrmzajV39irzJ17ARq1uNbkKXdrADF73DZyp",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 78.5,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Recreation",
                "Gyms and Fitness Centers"
            ],
            "category_id": "17018000",
            "date": "2021-04-08",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Touchstone",
            "name": "Touchstone Climbing",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "egbgkyBVeJU9WZNXQBjJtQwl3o1XGbSMRGkRK",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": -500,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Travel",
                "Airlines and Aviation Services"
            ],
            "category_id": "22001000",
            "date": "2021-04-08",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "United Airlines",
            "name": "United Airlines",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "4xMxepW8yVfLPD1gkREZTjPXR4NzbqugNPMNr",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 12,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Food and Drink",
                "Restaurants",
                "Fast Food"
            ],
            "category_id": "13005032",
            "date": "2021-04-07",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": "3322"
            },
            "merchant_name": "McDonald's",
            "name": "McDonald's",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "GWjWlR4PKkir74nwbL5bT5DDr6aVrXUW6ZGLw",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 4.33,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Food and Drink",
                "Restaurants",
                "Coffee Shop"
            ],
            "category_id": "13005043",
            "date": "2021-04-07",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Starbucks",
            "name": "Starbucks",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "nyBykb7VxJf5kaLERlKRtKJJpvjBp9C8AJ3Xg",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 89.4,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Food and Drink",
                "Restaurants"
            ],
            "category_id": "13005000",
            "date": "2021-04-06",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Sparkfun",
            "name": "SparkFun",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "bL8LAwKVPjf5PNdzjK4jtN33z6ZDzWfGmBoJv",
            "transaction_type": "place",
            "unofficial_currency_code": null
        },
        {
            "account_id": "ALqLG4z3y9fzdkxbXNZNuMK9DPwxGwUWB5wLn",
            "account_owner": null,
            "amount": -4.22,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Transfer",
                "Credit"
            ],
            "category_id": "21005000",
            "date": "2021-04-05",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": null,
            "name": "INTRST PYMNT",
            "payment_channel": "other",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "1zPzkLgp7wuaDE5VmRqgIxbxpBwMJquQNzVm3",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "nyBykb7VxJf5kaLERlKltVJk1vjzLjt8JQk9x",
            "account_owner": null,
            "amount": 500,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Travel",
                "Airlines and Aviation Services"
            ],
            "category_id": "22001000",
            "date": "2021-03-26",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "United Airlines",
            "name": "United Airlines",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "Jn1nrQjeBbt3ZK7wEo5otlLoNqjgypubno5Ax",
            "transaction_type": "special",
            "unofficial_currency_code": null
        },
        {
            "account_id": "WaKaQpWGq4i1LVZ7qo5oCRNm8jAJrAuWBjM3d",
            "account_owner": null,
            "amount": 6.33,
            "authorized_date": null,
            "authorized_datetime": null,
            "category": [
                "Travel",
                "Taxi"
            ],
            "category_id": "22016000",
            "date": "2021-03-24",
            "datetime": null,
            "iso_currency_code": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "merchant_name": "Uber",
            "name": "Uber 072515 SF**POOL**",
            "payment_channel": "in store",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "9PGPL5mnywFzlbxD9raruJNRgPy7KnflKVErl",
            "transaction_type": "special",
            "unofficial_currency_code": null
        }
    ]}
""".trimIndent()
