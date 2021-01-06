create table if not exists public.user
(
id varchar(20) not null constraint user_pk primary key,
login varchar(20) not null constraint user_uk unique ,
name varchar(100) not null,
salary numeric(20,10) not null,
start_date timestamp not null,
created_by varchar(20) not null,
created_at timestamp not null,
updated_by varchar(20),
updated_at timestamp
);