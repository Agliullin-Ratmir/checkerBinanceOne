--liquibase formatted sql

--changeset r.agliullin:2020-06-11--01-01
CREATE TYPE flow_state AS ENUM ('ADD_TICKET_TITLE', 'ADD_LOWER_PRICE', 'ADD_HIGHER_PRICE');
--rollback drop table if exists flow_state;

--changeset r.agliullin:2020-06-11--01-02
create table state_log (
                           state_log_id bigserial primary key not null,
                           chat_id text,
                           current_state flow_state,
                           ticket_title text,
                           lower_price float,
                           modify_date timestamp default now()
);
alter table public.state_log add constraint state_log_fk
    foreign key (state_log_id) references public.state_log;
-- rollback drop table if exists public.state_log;