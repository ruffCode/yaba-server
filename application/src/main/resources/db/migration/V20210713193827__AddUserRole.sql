DO $$ BEGIN
    CREATE TYPE user_role AS ENUM( 'USER', 'ADMIN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

alter table users_table
    add column if not exists role text default 'USER';

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