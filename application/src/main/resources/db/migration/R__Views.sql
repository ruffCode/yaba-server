CREATE or REPLACE VIEW users
AS
SELECT id,
       email,
       users_table.active,
       users_table.verified,
       created_at,
       updated_at,
       users_table.role
FROM users_table;

create or replace VIEW transactions
AS
SELECT t.id,
       t.plaid_transaction_id,
       t.account_id,
       a.plaid_account_id,
       a.item_id,
       a.plaid_item_id,
       a.user_id,
       t.category,
       t.subcategory,
       t.type,
       t.name,
       t.amount,
       t.iso_currency_code,
       t.unofficial_currency_code,
       t.date,
       t.pending,
       t.account_owner,
       t.created_at,
       t.updated_at,
       t.merchant_name
FROM transactions_table t
         LEFT JOIN accounts a ON t.account_id = a.id;

CREATE OR REPLACE VIEW items
AS
SELECT id,
       plaid_item_id,
       user_id,
       plaid_access_token,
       plaid_institution_id,
       status,
       created_at,
       updated_at,
       linked,
       times_unlinked
FROM items_table;

CREATE OR REPLACE VIEW accounts
AS
SELECT a.id,
       a.plaid_account_id,
       a.item_id,
       i.plaid_item_id,
       i.user_id,
       a.name,
       a.mask,
       a.official_name,
       a.current_balance,
       a.available_balance,
       a.iso_currency_code,
       a.unofficial_currency_code,
       a.type,
       a.subtype,
       a.created_at,
       a.updated_at,
       a.hidden,
       a.credit_limit
FROM accounts_table a
         LEFT JOIN items i ON i.id = a.item_id;