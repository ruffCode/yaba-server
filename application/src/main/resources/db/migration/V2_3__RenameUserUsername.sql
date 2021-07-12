drop view users;

alter table users_table
    add column if not exists verified bool not null default true;

alter table users_table
    add column if not exists reset_token text default null;

alter table users_table
    add column if not exists active bool not null default true;

CREATE or REPLACE VIEW users
AS
SELECT id,
       email,
       users_table.active,
       users_table.verified,
       created_at,
       updated_at
FROM users_table;
