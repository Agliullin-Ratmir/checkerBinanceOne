--liquibase formatted sql

--changeset r.agliullin:2020-06-26--02-01
alter table public.price_range alter column user_price_range_fk drop not null;
-- rollback alter table public.price_range alter column user_price_range_fk set not null;

--changeset r.agliullin:2020-06-26--02-02
alter table public.user_price_range alter column price_range_fk drop not null;
-- rollback alter table public.user_price_range alter column price_range_fk set not null;

--changeset r.agliullin:2020-06-26--02-03
alter table public.ticket alter column user_price_range_fk drop not null;
-- rollback alter table public.ticket alter column user_price_range_fk set not null;

--changeset r.agliullin:2020-06-26--02-04
alter table public.user_price_range alter column ticket_fk drop not null;
-- rollback alter table public.user_price_range alter column ticket_fk set not null;