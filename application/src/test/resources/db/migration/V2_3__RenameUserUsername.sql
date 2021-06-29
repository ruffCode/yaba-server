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
