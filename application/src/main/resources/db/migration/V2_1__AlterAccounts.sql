alter table accounts_table
    add column if not exists hidden bool default false;




CREATE OR REPLACE VIEW  accounts
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
       a.hidden
FROM accounts_table a
         LEFT JOIN items i ON i.id = a.item_id;
