-- Smart Toll System - MySQL seed data
-- The backend DataSeeder performs the same job automatically on first boot (H2 & MySQL).
-- Provided here for manual MySQL setup.

USE smarttoll;

-- Admin user (password: admin123, BCrypt hash)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN');

INSERT INTO rfid_tags (tag_id, active) VALUES
('RFID001', 1), ('RFID002', 1), ('RFID003', 1), ('RFID004', 1), ('RFID005', 1), ('RFID006', 1), ('RFID007', 1);

INSERT INTO vehicles (vehicle_type, registration_number, owner_name, active, rfid_tag_id, number_of_seats, load_capacity, passenger_capacity, emergency_service) VALUES
('CAR', 'TN01AB1234', 'Arun',     1, 1, 5,  NULL, NULL, NULL),
('TRUCK', 'TN02CD5678', 'Kumar',  1, 2, NULL, 12,  NULL, NULL),
('BUS', 'TN03EF9012', 'Ravi',     1, 3, NULL, NULL, 48,  NULL),
('CAR', 'TN04GH3456', 'Gobinath', 1, 4, 5,  NULL, NULL, NULL),
('EMERGENCY', 'TN05IJ7890', 'City Ambulance', 1, 5, NULL, NULL, NULL, 'Ambulance'),
('TRUCK', 'TN06KL2345', 'Priya',  0, 6, NULL, 20,  NULL, NULL);