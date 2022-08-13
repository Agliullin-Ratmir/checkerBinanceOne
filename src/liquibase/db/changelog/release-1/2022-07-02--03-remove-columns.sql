--liquibase formatted sql

--changeset r.agliullin:2020-07-02--03-01
alter table public.user_price_range drop ticket_fk;
-- rollback alter table public.price_range add column ticket_fk bigint not null;

--changeset r.agliullin:2020-07-02--03-02
alter table public.price_range drop ticket_fk;
-- rollback alter table public.price_range add column ticket_fk bigint not null;