CREATE TABLE users(
    login text not null primary key,
    password text not null,
    role text not null,
    is_active bool default false,
    active_last timestamp
);