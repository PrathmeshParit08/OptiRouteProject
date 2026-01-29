-- Users (password is 'password')
INSERT INTO users (username, password) VALUES ('user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

-- Locations
INSERT INTO locations (name, type) VALUES ('New York', 'CITY');
INSERT INTO locations (name, type) VALUES ('London', 'CITY');
INSERT INTO locations (name, type) VALUES ('Paris', 'CITY');
INSERT INTO locations (name, type) VALUES ('Tokyo', 'CITY');
INSERT INTO locations (name, type) VALUES ('Sydney', 'CITY');
INSERT INTO locations (name, type) VALUES ('JFK Airport', 'AIRPORT');
INSERT INTO locations (name, type) VALUES ('Heathrow Airport', 'AIRPORT');

-- Routes (Direct only)
-- NY -> London (Multiple options)
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 420, 500.0);
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 450, 350.0);
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 400, 800.0);

-- London -> Paris
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'TRAIN', 135, 100.0);
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'FLIGHT', 75, 150.0);
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'BUS', 480, 40.0);

-- NY -> Tokyo
INSERT INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 4, 'FLIGHT', 840, 1200.0);
