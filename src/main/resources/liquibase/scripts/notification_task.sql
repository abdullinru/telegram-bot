-- liquibase formatted sql
-- changeset abdullinru:1
create table notification_task(
    id          SERIAL,
    chart_id    INTEGER,
    message     text,
    date_time   TIMESTAMP

)