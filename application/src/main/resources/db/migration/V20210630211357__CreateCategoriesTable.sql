create table if not exists categories
(
    id        int primary key not null,
    grp       text            not null,
    hierarchy text[]          not null
);