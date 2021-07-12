create table if not exists transaction_updates
(
    id         uuid        default gen_random_uuid() primary key,
    user_id    uuid not null,
    added      text[],
    removed    text[],
    created_at timestamptz default now(),
    foreign key (user_id) references users_table (id)
        on delete cascade
);