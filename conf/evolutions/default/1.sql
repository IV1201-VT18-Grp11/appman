# --- !Ups

CREATE TABLE users (
  users_id SERIAL NOT NULL PRIMARY KEY,
  username TEXT NOT NULL,
  name     TEXT,
  surname  TEXT,
  password TEXT NOT NULL,
  email    TEXT,
  employee BOOLEAN
);

CREATE TABLE availability (
  availability_id SERIAL PRIMARY KEY,
  users_id        INTEGER REFERENCES users,
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
  field_id  INTEGER REFERENCES field,
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
