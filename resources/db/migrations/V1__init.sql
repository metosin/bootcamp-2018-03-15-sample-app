--
-- Initial DB schema:
--

create schema if not exists app;

create table app.messages (
  id uuid primary key,
  message text
);
