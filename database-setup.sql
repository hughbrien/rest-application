-- MySQL Database Setup Script for Rest Application
-- Run this script to create the database and initial data

-- Create the database
CREATE DATABASE IF NOT EXISTS restapp_db;
USE restapp_db;

-- Create user table (will be auto-created by Hibernate, but here for reference)
-- CREATE TABLE IF NOT EXISTS users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     email VARCHAR(255) NOT NULL UNIQUE,
--     age INT
-- );

-- Insert some sample data
INSERT IGNORE INTO users (name, email, age) VALUES
('John Doe', 'john.doe@example.com', 30),
('Jane Smith', 'jane.smith@example.com', 25),
('Bob Johnson', 'bob.johnson@example.com', 35),
('Alice Williams', 'alice.williams@example.com', 28),
('Charlie Brown', 'charlie.brown@example.com', 32);

-- Create a user for the application (optional - adjust as needed)
-- CREATE USER IF NOT EXISTS 'restapp_user'@'localhost' IDENTIFIED BY 'restapp_password';
-- GRANT ALL PRIVILEGES ON restapp_db.* TO 'restapp_user'@'localhost';
-- FLUSH PRIVILEGES;
