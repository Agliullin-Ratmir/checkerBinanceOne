--liquibase formatted sql

--changeset r.agliullin:2020-07-02--02-01
alter table public.price_range add column
    ticket_fk bigint not null;
-- rollback alter table public.user_price_range drop ticket_fk;

--changeset r.agliullin:2020-07-02--02-02
alter table public.ticket drop user_price_range_fk;
-- rollback alter table public.ticket add column user_price_range_fk bigint not null;

--changeset r.agliullin:2020-07-02--02-03
alter table public.ticket drop price_range_fk;
-- rollback alter table public.ticket add column price_range_fk bigint not null;