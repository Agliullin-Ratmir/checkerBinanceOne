--liquibase formatted sql

--changeset r.agliullin:2020-06-26--01-01
alter table public.user_price_range add column
    ticket_fk bigint not null;
-- rollback alter table public.user_price_range drop ticket_fk;