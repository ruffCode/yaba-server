alter table user_push_tokens
    drop constraint user_push_tokens_user_id_fkey,
    add constraint user_push_tokens_user_id_fkey
        foreign key (user_id)
            references users_table (id)
            on delete cascade;