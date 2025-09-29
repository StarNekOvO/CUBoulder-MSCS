create table registration_requests
(
    id                serial primary key,
    email             varchar not null,
    confirmation_code uuid    not null,
    created_at        timestamp default current_timestamp
);

create table registrations
(
    id         serial primary key,
    email      varchar not null,
    created_at timestamp default current_timestamp
);
