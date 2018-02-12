# --- !Ups

CREATE TABLE users (
  id        SERIAL NOT NULL PRIMARY KEY,
  username  TEXT   NOT NULL UNIQUE,
  firstname TEXT,
  surname   TEXT,
  password  TEXT   NOT NULL,
  email     TEXT UNIQUE,
  employee  BOOLEAN
);

CREATE TABLE sessions (
  id        SERIAL    NOT NULL PRIMARY KEY,
  "user"    INTEGER   NOT NULL REFERENCES users,
  "from"    TIMESTAMP NOT NULL DEFAULT now(),
  refreshed TIMESTAMP NOT NULL DEFAULT now(),
  deleted   BOOLEAN   NOT NULL DEFAULT FALSE
);

CREATE TABLE fields (
  id   SERIAL NOT NULL PRIMARY KEY,
  NAME TEXT
);

CREATE TABLE jobs (
  id        SERIAL    NOT NULL PRIMARY KEY,
  field     INTEGER   NOT NULL REFERENCES fields,
  name      TEXT      NOT NULL,
  from_date TIMESTAMP NOT NULL,
  to_date   TIMESTAMP,
  country   TEXT,
  description TEXT  NOT NULL,
  requirement TEXT NOT NULL
);

CREATE TABLE application (
  id          SERIAL  NOT NULL PRIMARY KEY,
  "user"      INTEGER NOT NULL REFERENCES users,
  job         INTEGER NOT NULL REFERENCES jobs,
  description TEXT
);

CREATE TABLE availability (
  id          SERIAL NOT NULL PRIMARY KEY,
  application INTEGER REFERENCES application,
  from_date   DATE,
  to_date     DATE
);

CREATE TABLE competences (
  id   SERIAL NOT NULL PRIMARY KEY,
  name TEXT
);

CREATE TABLE application_competences (
  competence          INTEGER REFERENCES competences,
  years_of_experience FLOAT,
  application         INTEGER REFERENCES application
);


# --- !Downs
DROP TABLE application_competences;
DROP TABLE competences;
DROP TABLE availability;
DROP TABLE application;
DROP TABLE jobs;
DROP TABLE fields;
DROP TABLE sessions;
DROP TABLE users;