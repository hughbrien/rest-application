package com.hugenet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing database connection...");
        
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Database connected successfully!");
            logger.info("Database: {}", connection.getMetaData().getDatabaseProductName());
            logger.info("URL: {}", connection.getMetaData().getURL());
            
            // Check if we have any users in the database
            long userCount = userRepository.count();
            logger.info("Current user count: {}", userCount);
            
            // If no users exist, create some sample data
            if (userCount == 0) {
                logger.info("No users found. Creating sample data...");
                createSampleUsers();
            }
            
        } catch (Exception e) {
            logger.error("Database connection failed: {}", e.getMessage());
            logger.warn("Application will continue without database connectivity");
        }
    }
    
    private void createSampleUsers() {
        try {
            User user1 = new User("John Doe", "john.doe@example.com", 30);
            User user2 = new User("Jane Smith", "jane.smith@example.com", 25);
            User user3 = new User("Bob Johnson", "bob.johnson@example.com", 35);
            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            
            logger.info("Sample users created successfully");
        } catch (Exception e) {
            logger.error("Failed to create sample users: {}", e.getMessage());
        }
    }
}
