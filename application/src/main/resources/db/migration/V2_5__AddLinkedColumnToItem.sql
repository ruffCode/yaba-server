
alter table items_table
add column linked bool default true;

CREATE  OR REPLACE VIEW items
AS
SELECT
    id,
    plaid_item_id,
    user_id,
    plaid_access_token,
    plaid_institution_id,
    status,
    created_at,
    updated_at,
    linked
FROM
    items_table;
