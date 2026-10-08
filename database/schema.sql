-- Smart Toll System - MySQL schema
-- Applied automatically by Hibernate in the backend; kept here as reference + for manual MySQL setup.
-- Usage:  mysql -u root -p smarttoll < schema.sql

CREATE DATABASE IF NOT EXISTS smarttoll CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smarttoll;

CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL DEFAULT 'ADMIN',
    created_at DATETIME(6)
);

CREATE TABLE rfid_tags (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_id     VARCHAR(255) NOT NULL UNIQUE,
    active     BIT NOT NULL DEFAULT 1,
    issue_date DATETIME(6)
);

CREATE TABLE vehicles (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_type        VARCHAR(20) NOT NULL,
    registration_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name          VARCHAR(255) NOT NULL,
    active              BIT NOT NULL DEFAULT 1,
    created_at          DATETIME(6),
    rfid_tag_id         BIGINT,
    number_of_seats     INT,
    load_capacity       DOUBLE,
    passenger_capacity  INT,
    emergency_service   VARCHAR(255),
    CONSTRAINT fk_vehicle_tag FOREIGN KEY (rfid_tag_id) REFERENCES rfid_tags (id)
);

CREATE TABLE toll_transactions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id   VARCHAR(255) NOT NULL UNIQUE,
    vehicle_id       BIGINT,
    rfid_tag_id      VARCHAR(255),
    base_toll        DOUBLE NOT NULL,
    traffic_charge   DOUBLE NOT NULL,
    peak_charge      DOUBLE NOT NULL,
    weather_charge   DOUBLE NOT NULL,
    pollution_charge DOUBLE NOT NULL,
    final_amount     DOUBLE NOT NULL,
    status           VARCHAR(255) NOT NULL,
    created_at       DATETIME(6),
    CONSTRAINT fk_tx_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
);

CREATE TABLE payments (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT,
    payment_method VARCHAR(255) NOT NULL,
    payment_id     VARCHAR(255) NOT NULL UNIQUE,
    amount         DOUBLE NOT NULL,
    status         VARCHAR(255) NOT NULL,
    created_at     DATETIME(6),
    CONSTRAINT fk_pay_tx FOREIGN KEY (transaction_id) REFERENCES toll_transactions (id)
);