BEGIN;

-- Insert a DJ user (allowed role: ROLE_DJ)
INSERT INTO users (username, password_hash, role)
VALUES ('djUser', '$2a$08$randomhashforDJ', 'ROLE_DJ');

-- Insert a Host user (allowed role: ROLE_HOST)
INSERT INTO users (username, password_hash, role)
VALUES ('hostUser', '$2a$08$randomhashforHost', 'ROLE_HOST');

-- Insert a test event created by djUser (assuming djUser gets user_id = 1)
INSERT INTO events (event_name, event_date, start_time, end_time, created_by)
VALUES ('Test Event', '2025-03-10', '18:00:00', '21:00:00', 1);

-- Assign hostUser (assumed user_id = 2) as a host for the event (assumed event_id = 1)
INSERT INTO event_hosts (event_id, user_id)
VALUES (1, 2);

COMMIT;
