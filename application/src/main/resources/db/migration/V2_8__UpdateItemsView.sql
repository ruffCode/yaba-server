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
