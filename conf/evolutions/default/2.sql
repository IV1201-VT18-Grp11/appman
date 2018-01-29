# --- !Ups
INSERT INTO users (id, username, name, surname, password, email, employee)
VALUES (1, 'laserkitten', 'Teo', 'Klestrup', '088d0246167347e47d4ded8fa09fcbc47e23465288a065d106d2660591d6ece4&ee9cb2f143cbbca063350e10fc8fde07b48de6a91561bf4d144fd789c960fce4670ace78d694d2132824b9795516f2a6bb8b6144074c48b63e4135e93cd804d0', 'teo@nullable.se', FALSE); -- Password: 1234

INSERT INTO users (id, username, name, surname, password, email, employee)
VALUES (2, 'donald_duck', 'Donald', 'Duck', 'fa05f690411d293aa3c8aa59195500a8ecb235b36ad2c3f149f92218caf837ca&0fbb4db850980a123442fc9a042fd5c7eeded847365c689191ca450e24b835c36703a6c5ad96dcd8d2ce519d303194c8a289840fa0a9eb614cedb48d62b0ff25', 'donald@duckburg.com', FALSE); -- Password: 123456

INSERT INTO availability (id, "user", from_date, to_date)
VALUES (1, 1, '2018-01-01', '2019-01-01');

INSERT INTO fields (id, name)
VALUES (1, 'Software');

INSERT INTO jobs (id, field, name, from_date, to_date, country)
VALUES (1, 1, 'Scala Magician', '2018-02-01', '2018-10-01', 'Sweden');

ALTER SEQUENCE users_id_seq MINVALUE 3 START WITH 3 RESTART WITH 3;
ALTER SEQUENCE availability_id_seq MINVALUE 2 START WITH 2 RESTART WITH 2;
ALTER SEQUENCE fields_id_seq MINVALUE 2 START WITH 2 RESTART WITH 2;
ALTER SEQUENCE jobs_id_seq MINVALUE 2 START WITH 2 RESTART WITH 2;
ALTER SEQUENCE fields_id_seq MINVALUE 2 START WITH 2 RESTART WITH 2;

# --- !Downs
DELETE FROM availability;
DELETE FROM users;
DELETE FROM jobs;
DELETE FROM fields;

ALTER SEQUENCE users_id_seq MINVALUE 1 START WITH 1 RESTART WITH 1;
ALTER SEQUENCE availability_id_seq MINVALUE 1 START WITH 1 RESTART WITH 1;
ALTER SEQUENCE fields_id_seq MINVALUE 1 START WITH 1 RESTART WITH 1;
ALTER SEQUENCE jobs_id_seq MINVALUE 1 START WITH 1 RESTART WITH 1;
ALTER SEQUENCE fields_id_seq MINVALUE 1 START WITH 1 RESTART WITH 1;
