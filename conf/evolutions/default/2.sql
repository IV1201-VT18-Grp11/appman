# --- !Ups
INSERT INTO users (id, username, firstname, surname, password, email, "role")
VALUES (1, 'laserkitten', 'Teo', 'Klestrup',
        '088d0246167347e47d4ded8fa09fcbc47e23465288a065d106d2660591d6ece4&ee9cb2f143cbbca063350e10fc8fde07b48de6a91561bf4d144fd789c960fce4670ace78d694d2132824b9795516f2a6bb8b6144074c48b63e4135e93cd804d0',
        'teo@nullable.se', 'Applicant'); -- Password: 1234

INSERT INTO users (id, username, firstname, surname, password, email, "role")
VALUES (2, 'donald_duck', 'Donald', 'Duck',
        'fa05f690411d293aa3c8aa59195500a8ecb235b36ad2c3f149f92218caf837ca&0fbb4db850980a123442fc9a042fd5c7eeded847365c689191ca450e24b835c36703a6c5ad96dcd8d2ce519d303194c8a289840fa0a9eb614cedb48d62b0ff25',
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


INSERT INTO applications (id, "user", job, description)
VALUES (1, 1, 1, 'I am awesome');

INSERT INTO applications (id, "user", job, description)
VALUES (2, 1, 2, 'I am great');


INSERT INTO availabilities (id, application, from_date, to_date)
VALUES (1, 1, '2018-01-01', '2019-01-01');


INSERT INTO availabilities (id, application, from_date, to_date)
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
MINVALUE 2
START WITH 2 RESTART WITH 2;
ALTER SEQUENCE fields_id_seq
MINVALUE 2
START WITH 2 RESTART WITH 2;
ALTER SEQUENCE jobs_id_seq
MINVALUE 2
START WITH 2 RESTART WITH 2;
ALTER SEQUENCE applications_id_seq
MINVALUE 2
START WITH 2 RESTART WITH 2;
ALTER SEQUENCE availabilities_id_seq
MINVALUE 2
START WITH 2 RESTART WITH 2;
ALTER SEQUENCE competences_id_seq
MINVALUE 2
START WITH 2 RESTART WITH 2;


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
