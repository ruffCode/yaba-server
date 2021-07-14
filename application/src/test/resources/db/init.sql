CREATE EXTENSION if not exists pg_trgm;
CREATE TABLE users_table
(
    id         uuid                 DEFAULT gen_random_uuid() PRIMARY KEY,
    username   text UNIQUE NOT NULL,
    password   text        not null default '',
    created_at timestamptz          default now(),
    updated_at timestamptz          default now()
);

CREATE VIEW users
AS
SELECT id,
       username,
       created_at,
       updated_at
FROM users_table;


CREATE TABLE items_table
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


CREATE VIEW items
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


CREATE TABLE accounts_table
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
    hidden                   bool        default false,
    created_at               timestamptz default now(),
    updated_at               timestamptz default now()
);

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
       a.hidden
FROM accounts_table a
         LEFT JOIN items i ON i.id = a.item_id;


CREATE TABLE transactions_table
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

CREATE VIEW transactions
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


CREATE TABLE link_events_table
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

CREATE TABLE plaid_api_events_table
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

create table if not exists institutions_table
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
create table if not exists user_push_tokens
(
    token   text primary key,
    user_id uuid not null,
    foreign key (user_id) references users_table (id)
);


drop view users;

alter table users_table
    rename username to email;

alter table users_table
    add column verified bool not null default true;

alter table users_table
    add column reset_token text default null;

alter table users_table
    add column active bool not null default true;

CREATE or REPLACE VIEW users
AS
SELECT id,
       email,
       users_table.active,
       users_table.verified,
       created_at,
       updated_at
FROM users_table;
alter table users_table
    alter column verified set default false;

alter table items_table
    add column linked bool default true;

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
       linked
FROM items_table;

alter table items_table
    add column times_unlinked smallint default 0;

create or replace function increment_times_unlinked() returns trigger
    language plpgsql
as
$$
BEGIN
    if new.linked=false and old.linked= true then
        NEW.times_unlinked = OLD.times_unlinked + 1;
    end if;
    RETURN NEW;
END;
$$;

CREATE TRIGGER items_times_unlinked
    BEFORE UPDATE
    ON items_table
    FOR EACH ROW
EXECUTE PROCEDURE increment_times_unlinked();

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

create table if not exists categories
(
    id        int primary key not null,
    grp       text            not null,
    hierarchy text[]          not null
);

alter table transactions_table
    add column if not exists merchant_name text default null;

alter table transactions_table
    add column if not exists category_id int default null;

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

create table if not exists transaction_updates
(
    id      uuid default gen_random_uuid() primary key,
    user_id uuid not null,
    added   text[],
    removed text[],
    created_at timestamptz default now(),
    foreign key (user_id) references users_table (id)
        on delete cascade
);

DO $$ BEGIN
    CREATE TYPE user_role AS ENUM( 'USER', 'ADMIN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

alter table users_table
add column role text default 'USER';

DROP VIEW IF EXISTS users;
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

create table if not exists last_login_table
(
    user_id    uuid primary key not null,
    last_login timestamptz      not null,
    foreign key (user_id) references users_table (id)
        on delete cascade
);

alter table user_push_tokens
    drop constraint user_push_tokens_user_id_fkey,
    add constraint user_push_tokens_user_id_fkey
        foreign key (user_id)
            references users_table (id)
            on delete cascade;