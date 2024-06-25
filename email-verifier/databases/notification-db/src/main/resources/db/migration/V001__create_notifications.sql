create table notifications
(
    id                serial primary key,
    email             varchar not null,
    confirmation_code uuid    not null,
    created_at        timestamp default current_timestamp
);
