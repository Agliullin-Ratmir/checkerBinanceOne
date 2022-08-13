--liquibase formatted sql

--changeset r.agliullin:2020-07-11--01-01
alter type public.flow_state add value 'UPDATE_LOWER_PRICE';
-- rollback select 1;

--changeset r.agliullin:2020-07-11--01-02
alter type public.flow_state add value 'UPDATE_HIGHER_PRICE';
-- rollback select 1;

--changeset r.agliullin:2020-07-11--01-03
alter table public.state_log add column
    updating_ticket_id bigint;
-- rollback alter table public.state_log drop updating_ticket_id;