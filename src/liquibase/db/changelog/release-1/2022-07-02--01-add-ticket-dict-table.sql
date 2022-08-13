--liquibase formatted sql

--changeset r.agliullin:2020-07-02--01-01
create table public.ticket_dictionary (
                                         ticket_dictionary_id bigserial primary key not null,
                                         title text,
                                         amount bigint not null,
                                         modify_date timestamp default now()
);
alter table public.ticket_dictionary add constraint ticket_dictionary_fk
    foreign key (ticket_dictionary_id) references public.ticket_dictionary;
-- rollback drop table if exists public.ticket_dictionary;