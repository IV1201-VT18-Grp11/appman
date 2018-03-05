# --- !Ups
INSERT INTO fields (id, name)
VALUES (1, 'Software')
ON CONFLICT DO NOTHING;

INSERT INTO fields (id, name)
VALUES (2, 'Food and Wine')
ON CONFLICT DO NOTHING;

INSERT INTO jobs (id, field, name, from_date, to_date, country, description, requirement)
VALUES (1, 1, 'Scala Magician', '2018-02-01', '2018-10-01', 'Sweden',
        'Wonderful experience that will make you grow as a developer.',
        'Inner beauty.')
ON CONFLICT DO NOTHING;

INSERT INTO jobs (id, field, name, from_date, to_date, country, description, requirement)
VALUES (2, 2, 'Sous Chef', '2018-03-01', '2018-10-01', 'Denmark',
        'Yummy', 'Cooking skills')
ON CONFLICT DO NOTHING;

# --- !Downs
