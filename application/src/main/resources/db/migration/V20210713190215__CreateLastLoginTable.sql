create table if not exists last_login_table
(
    user_id    uuid primary key not null,
    last_login timestamptz      not null,
    foreign key (user_id) references users_table (id)
        on delete cascade
);