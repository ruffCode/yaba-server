alter table transactions_table
    add column if not exists merchant_name text default null;

alter table transactions_table
    add column if not exists category_id int default null;