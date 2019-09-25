CREATE KEYSPACE analytics
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

USE analytics;

CREATE TABLE events (
    id uuid,
	type text,
    site text,
    timestamp timestamp,
	version int,
	source text,
    PRIMARY KEY (id, type, site)
);