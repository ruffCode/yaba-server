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