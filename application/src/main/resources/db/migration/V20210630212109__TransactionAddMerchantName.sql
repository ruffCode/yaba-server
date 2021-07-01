alter table transactions_table
    add column merchant_name text default null;

alter table transactions_table
    add column category_id int default null;