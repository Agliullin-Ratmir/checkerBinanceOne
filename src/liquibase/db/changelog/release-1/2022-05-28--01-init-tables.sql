--liquibase formatted sql

--changeset r.agliullin:2020-05-28--01-01
create table public.price_range (
                                         price_range_id bigserial primary key not null,
                                         lower_price float,
                                         higher_price float,
                                         user_price_range_fk bigint not null,
                                         modify_date timestamp default now()
);
alter table public.price_range add constraint price_range_fk
    foreign key (price_range_id) references public.price_range;
-- rollback drop table if exists public.price_range;

--changeset r.agliullin:2020-05-28--01-02
create table public.user_price_range (
  user_price_range_id bigserial primary key not null,
  chat_id text,
  price_range_fk bigint not null,
  modify_date timestamp default now()
);
alter table public.user_price_range add constraint user_price_range_fk
    foreign key (user_price_range_id) references public.user_price_range;
-- rollback drop table if exists public.user_price_range;

--changeset r.agliullin:2020-05-28--01-03
create table public.ticket (
                                         ticket_id bigserial primary key not null,
                                         ticket_title text,
                                         user_price_range_fk bigint not null,
                                         modify_date timestamp default now()
);
alter table public.ticket add constraint ticket_fk
    foreign key (ticket_id) references public.ticket;
-- rollback drop table if exists public.ticket;
