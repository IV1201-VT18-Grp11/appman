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

CREATE TABLE fields (
  field_id SERIAL PRIMARY KEY,
  name     TEXT
);

CREATE TABLE jobs (
  job_id    SERIAL NOT NULL PRIMARY KEY,
  field_id  INTEGER NOT NULL REFERENCES fields,
  name      TEXT NOT NULL ,
  from_date TIMESTAMP NOT NULL ,
  to_date   TIMESTAMP,
  country   TEXT
);

# --- !Downs
DROP TABLE availability;
DROP TABLE users;
DROP TABLE competence;
DROP TABLE jobs;
DROP TABLE fields;
