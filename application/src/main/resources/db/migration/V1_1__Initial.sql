CREATE EXTENSION if not exists pgcrypto;
CREATE EXTENSION if not exists pg_trgm;

-- This trigger updates the value in the updated_at column. It is used in the tables below to log
-- when a row was last updated.

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP VIEW IF EXISTS users cascade ;
DROP VIEW IF EXISTS items cascade ;
DROP VIEW IF EXISTS transactions cascade ;
DROP VIEW IF EXISTS accounts cascade ;

-- USERS
-- This table is used to store the users of our application. The view returns the same data as the
-- table, we're just creating it to follow the pattern used in other tables.

CREATE TABLE IF NOT EXISTS users_table
(
    id         uuid                 DEFAULT gen_random_uuid() PRIMARY KEY,
    email      text UNIQUE NOT NULL,
    password   text        not null default '',
    created_at timestamptz          default now(),
    updated_at timestamptz          default now()
);


DROP TRIGGER IF EXISTS users_updated_at_timestamp on users_table;

CREATE TRIGGER users_updated_at_timestamp
    BEFORE UPDATE
    ON users_table
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();


CREATE OR REPLACE VIEW users
AS
SELECT id,
       email,
       created_at,
       updated_at
FROM users_table;


-- ITEMS
-- This table is used to store the items associated with each user. The view returns the same data
-- as the table, we're just using both to maintain consistency with our other tables. For more info
-- on the Plaid Item schema, see the docs page: https://plaid.com/docs/#item-schema

CREATE TABLE IF NOT EXISTS items_table
(
    id                   uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id              uuid REFERENCES users_table (id) ON DELETE CASCADE,
    plaid_access_token   text UNIQUE NOT NULL,
    plaid_item_id        text UNIQUE NOT NULL,
    plaid_institution_id text        NOT NULL,
    status               text        NOT NULL,
    created_at           timestamptz default now(),
    updated_at           timestamptz default now()
);

DROP TRIGGER IF EXISTS items_updated_at_timestamp on items_table;

CREATE TRIGGER items_updated_at_timestamp
    BEFORE UPDATE
    ON items_table
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();


CREATE OR REPLACE VIEW items
AS
SELECT id,
       plaid_item_id,
       user_id,
       plaid_access_token,
       plaid_institution_id,
       status,
       created_at,
       updated_at
FROM items_table;


-- ACCOUNTS
-- This table is used to store the accounts associated with each item. The view returns all the
-- data from the accounts table and some data from the items view. For more info on the Plaid
-- Accounts schema, see the docs page:  https://plaid.com/docs/#account-schema

CREATE TABLE IF NOT EXISTS accounts_table
(
    id                       uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    item_id                  uuid REFERENCES items_table (id) ON DELETE CASCADE,
    plaid_account_id         text UNIQUE NOT NULL,
    name                     text        NOT NULL,
    mask                     text        NOT NULL,
    official_name            text,
    current_balance          numeric(28, 10),
    available_balance        numeric(28, 10),
    iso_currency_code        text,
    unofficial_currency_code text,
    type                     text        NOT NULL,
    subtype                  text        NOT NULL,
    created_at               timestamptz default now(),
    updated_at               timestamptz default now()
);

DROP TRIGGER IF EXISTS accounts_updated_at_timestamp on accounts_table;

CREATE TRIGGER accounts_updated_at_timestamp
    BEFORE UPDATE
    ON accounts_table
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

DROP VIEW IF EXISTS accounts;

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
       a.updated_at
FROM accounts_table a
         LEFT JOIN items i ON i.id = a.item_id;


-- TRANSACTIONS
-- This table is used to store the transactions associated with each account. The view returns all
-- the data from the transactions table and some data from the accounts view. For more info on the
-- Plaid Transactions schema, see the docs page: https://plaid.com/docs/#transaction-schema

CREATE TABLE IF NOT EXISTS transactions_table
(
    id                       uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    account_id               uuid REFERENCES accounts_table (id) ON DELETE CASCADE,
    plaid_transaction_id     text UNIQUE     NOT NULL,
    plaid_category_id        text,
    category                 text,
    subcategory              text,
    type                     text            NOT NULL,
    name                     text            NOT NULL,
    amount                   numeric(28, 10) NOT NULL,
    iso_currency_code        text,
    unofficial_currency_code text,
    date                     date            NOT NULL,
    pending                  boolean         NOT NULL,
    account_owner            text,
    created_at               timestamptz default now(),
    updated_at               timestamptz default now()
);

DROP TRIGGER IF EXISTS transactions_updated_at_timestamp ON transactions_table;

CREATE TRIGGER transactions_updated_at_timestamp
    BEFORE UPDATE
    ON transactions_table
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

DROP VIEW IF EXISTS transactions;

CREATE OR REPLACE VIEW transactions
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
       t.updated_at
FROM transactions_table t
         LEFT JOIN accounts a ON t.account_id = a.id;


-- The link_events_table is used to log responses from the Plaid API for client requests to the
-- Plaid Link client. This information is useful for troubleshooting.

CREATE TABLE IF NOT EXISTS link_events_table
(
    id              uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    type            text NOT NULL,
    user_id         uuid,
    link_session_id text,
    request_id      text UNIQUE,
    error_type      text,
    error_code      text,
    created_at      timestamptz default now()
);


-- The plaid_api_events_table is used to log responses from the Plaid API for server requests to
-- the Plaid client. This information is useful for troubleshooting.

CREATE TABLE IF NOT EXISTS plaid_api_events_table
(
    id           SERIAL PRIMARY KEY,
    item_id      integer,
    plaid_method text NOT NULL,
    arguments    text,
    request_id   text UNIQUE,
    error_type   text,
    error_code   text,
    created_at   timestamptz default now()
);
create table IF NOT EXISTS institutions_table
(
    id                   uuid default gen_random_uuid() primary key,
    plaid_institution_id text not null unique,
    name                 text not null,
    products             text not null,
    country_codes        text not null,
    logo                 text,
    primary_color        text,
    url                  text,
    oauth                bool,
    routing_numbers      text
);
