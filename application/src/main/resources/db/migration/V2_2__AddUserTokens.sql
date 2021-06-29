create table if not exists user_push_tokens
(
    token   text primary key,
    user_id uuid not null,
    foreign key (user_id) references users_table (id)
);
