--liquibase formatted sql

--changeset r.agliullin:2020-05-31--01-01
create table tickets_user_price_ranges (
user_price_range_fk      bigint not null references
    public.user_price_range(user_price_range_id),
ticket_fk           bigint not null references
    public.ticket(ticket_id),
primary key (user_price_range_fk, ticket_fk)
);
--rollback drop table if exists tickets_user_price_ranges;