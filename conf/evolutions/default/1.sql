# --- !Ups

CREATE TABLE users (
  id       SERIAL NOT NULL PRIMARY KEY,
  username TEXT NOT NULL UNIQUE,
  firstname     TEXT,
  surname  TEXT,
  password TEXT NOT NULL,
  email    TEXT UNIQUE,
  employee BOOLEAN
);

CREATE TABLE sessions (
  id        SERIAL NOT NULL PRIMARY KEY,
  "user"    INTEGER NOT NULL REFERENCES users,
  "from"    TIMESTAMP NOT NULL DEFAULT now(),
  refreshed TIMESTAMP NOT NULL DEFAULT now(),
  deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE availability (
  id        SERIAL NOT NULL PRIMARY KEY,
  "user"    INTEGER REFERENCES users,
  from_date DATE,
  to_date   DATE
);

CREATE TABLE competences (
  id   SERIAL NOT NULL PRIMARY KEY,
  name TEXT
);

CREATE TABLE fields (
  id   SERIAL NOT NULL PRIMARY KEY,
  name TEXT
);

CREATE TABLE jobs (
  id        SERIAL NOT NULL PRIMARY KEY,
  field     INTEGER NOT NULL REFERENCES fields,
  name      TEXT NOT NULL,
  from_date TIMESTAMP NOT NULL,
  to_date   TIMESTAMP,
  country   TEXT
);

# --- !Downs
DROP TABLE availability;
DROP TABLE sessions;
DROP TABLE users;
DROP TABLE competences;
DROP TABLE jobs;
DROP TABLE fields;
