create table products (
  id serial primary key,
  name varchar(255) unique,
  quantity int not null default 0,
  created_at timestamp without time zone default now(),
  updated_at timestamp without time zone default now()
)