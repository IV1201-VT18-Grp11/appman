# --- !Ups

CREATE TABLE users (
  users_id SERIAL PRIMARY KEY,
  username TEXT,
  name     TEXT,
  surname  TEXT,
  password TEXT,
  email    TEXT
);

CREATE TABLE availability (
  availability_id SERIAL PRIMARY KEY,
  users_id        SERIAL REFERENCES users,
  from_date       DATE,
  to_date         DATE
);

CREATE TABLE competence (
  competence_id SERIAL PRIMARY KEY,
  name          TEXT
);

CREATE TABLE field (
  field_id SERIAL PRIMARY KEY,
  name     TEXT
);

CREATE TABLE job (
  job_id    SERIAL PRIMARY KEY,
  field_id  SERIAL REFERENCES field,
  name      TEXT,
  from_date DATE,
  to_date   DATE
);

# --- !Downs
DROP TABLE availability;
DROP TABLE users;
DROP TABLE competence;
DROP TABLE job;
DROP TABLE field;
