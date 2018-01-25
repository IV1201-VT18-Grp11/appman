# --- !Ups
INSERT INTO users (users_id, username, name, surname, password, email, employee)
VALUES (1, 'laserkitten', 'Teo', 'Klestrup', '1234', 'teo@nullable.se', FALSE);

INSERT INTO users (users_id, username, name, surname, password, email, employee)
VALUES (2, 'donald_duck', 'Donald', 'Duck', '123456', 'donald@duckburg.com', FALSE);

INSERT INTO availability (availability_id, users_id, from_date, to_date)
VALUES (1, 1, '2018-01-01', '2019-01-01');

INSERT INTO fields (field_id, name)
VALUES (1, 'Software');

INSERT INTO jobs (field_id, name, from_date, to_date, country)
VALUES (1, 'Scala Magician', '2018-02-01', '2018-10-01', 'Sweden');

# --- !Downs
DELETE FROM availability
WHERE availability_id = 1;

DELETE FROM users
WHERE users_id = 1;

DELETE FROM users
WHERE users_id = 2;

DELETE FROM jobs
WHERE field_id = 1;

DELETE FROM fields
WHERE field_id = 1;
