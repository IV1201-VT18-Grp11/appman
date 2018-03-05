# --- !Ups
INSERT INTO users (id, username, firstname, surname, password, email, "role")
VALUES (1, 'laserkitten', 'Teo', 'Klestrup',
        'fd3121de4272596a2c85ee9660786bd5ed33d320222ec9ac8975d3b03b6696b7&bb641aaec3761c463271b8d6314f07c1168d6c23acf6f8be4c4a368187d79b4c29cf73198619430c7e9ddbe3f477ecab473a38a7b488e8aa75733ed9b5879c73',
        'teo@nullable.se', 'Applicant'); -- Password: 1234

INSERT INTO users (id, username, firstname, surname, password, email, "role")
VALUES (2, 'donald_duck', 'Donald', 'Duck',
        '72b04713ed10d730253c8a048fbf897668d54f81da8c898e72372c4d4298384a&532799e05825ecb5ae63898501e60380e8678401e382c90ab1416a54861365b2e2da2606ec17555b2e8fe8c6e2c97eec1d42190864f2e58bb24380c43cb12a34',
        'donald@duckburg.com', 'Applicant'); -- Password: 123456

INSERT INTO fields (id, name)
VALUES (1, 'Software');

INSERT INTO fields (id, name)
VALUES (2, 'Food and Wine');

INSERT INTO jobs (id, field, name, from_date, to_date, country, description, requirement)
VALUES (1, 1, 'Scala Magician', '2018-02-01', '2018-10-01', 'Sweden',
        'Wonderful experience that will make you grow as a developer.',
        'Inner beauty.');

INSERT INTO jobs (id, field, name, from_date, to_date, country, description, requirement)
VALUES (2, 2, 'Sous Chef', '2018-03-01', '2018-10-01', 'Denmark',
        'Yummy', 'Cooking skills');


INSERT INTO applications (id, "user", job, description, date)
VALUES (1, 1, 1, 'I am awesome', now());

INSERT INTO applications (id, "user", job, description, date)
VALUES (2, 1, 2, 'I am great', now());


INSERT INTO availabilities (id, application, "from", "to")
VALUES (1, 1, '2018-01-01', '2019-01-01');


INSERT INTO availabilities (id, application, "from", "to")
VALUES (2, 2, '2018-01-01', '2019-01-01');

INSERT INTO competences (id, name)
VALUES (1, 'IT');

INSERT INTO competences (id, name)
VALUES (2, 'Cooking');

INSERT INTO application_competences (competence, years_of_experience, application)
VALUES (1, 11, 1);

INSERT INTO application_competences (competence, years_of_experience, application)
VALUES (2, 3, 2);

ALTER SEQUENCE users_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;
ALTER SEQUENCE availabilities_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;
ALTER SEQUENCE fields_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;
ALTER SEQUENCE jobs_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;
ALTER SEQUENCE applications_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;
ALTER SEQUENCE competences_id_seq
MINVALUE 3
START WITH 3 RESTART WITH 3;


# --- !Downs
DELETE FROM application_competences;
DELETE FROM competences;
DELETE FROM availabilities;
DELETE FROM applications;
DELETE FROM jobs;
DELETE FROM fields;
DELETE FROM sessions;
DELETE FROM users;

ALTER SEQUENCE users_id_seq
MINVALUE 1
START WITH 1 RESTART WITH 1;
ALTER SEQUENCE availabilities_id_seq
MINVALUE 1
START WITH 1 RESTART WITH 1;
ALTER SEQUENCE fields_id_seq
MINVALUE 1
START WITH 1 RESTART WITH 1;
ALTER SEQUENCE jobs_id_seq
MINVALUE 1
START WITH 1 RESTART WITH 1;
ALTER SEQUENCE fields_id_seq
MINVALUE 1
START WITH 1 RESTART WITH 1;
