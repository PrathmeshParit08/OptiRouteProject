-- Users (password is 'password')
INSERT IGNORE INTO users (username, email, password, mobile_number, security_question, security_answer) 
VALUES ('user', 'user@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '1234567890', 'Birthplace', 'Jaysingpur');  --changed ->Ignore
INSERT IGNORE INTO users (username, email, password, mobile_number, security_question, security_answer) 
VALUES ('admin', 'admin@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '0987654321', 'Birthplace', 'Jaysingpur');

-- Locations
INSERT IGNORE INTO locations (name, type) VALUES ('New York', 'CITY');
INSERT IGNORE INTO locations (name, type) VALUES ('London', 'CITY');
INSERT IGNORE INTO locations (name, type) VALUES ('Paris', 'CITY');
INSERT IGNORE INTO locations (name, type) VALUES ('Tokyo', 'CITY');
INSERT IGNORE INTO locations (name, type) VALUES ('Sydney', 'CITY');
INSERT IGNORE INTO locations (name, type) VALUES ('JFK Airport', 'AIRPORT');
INSERT IGNORE INTO locations (name, type) VALUES ('Heathrow Airport', 'AIRPORT');

-- Routes
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 420, 500.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 450, 350.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 2, 'FLIGHT', 400, 800.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'TRAIN', 135, 100.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'FLIGHT', 75, 150.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (2, 3, 'BUS', 480, 40.0);
INSERT IGNORE INTO direct_routes (from_location_id, to_location_id, transport_type, duration_minutes, cost) VALUES (1, 4, 'FLIGHT', 840, 1200.0);