package com.hugenet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserRepository userRepository;
    
    // Test database connection
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Testing database connection...");
            
            response.put("status", "success");
            response.put("message", "Database connection successful");
            response.put("database", connection.getMetaData().getDatabaseProductName());
            response.put("version", connection.getMetaData().getDatabaseProductVersion());
            response.put("url", connection.getMetaData().getURL());
            response.put("username", connection.getMetaData().getUserName());
            
            logger.info("Database connection test successful");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            logger.error("Database connection failed: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("error_code", e.getErrorCode());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Create a new user
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Creating new user: {}", user.getName());
            
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                response.put("status", "error");
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            User savedUser = userRepository.save(user);
            
            response.put("status", "success");
            response.put("message", "User created successfully");
            response.put("user", savedUser);
            
            logger.info("User created with ID: {}", savedUser.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error creating user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Fetching all users");
            
            List<User> users = userRepository.findAll();
            
            response.put("status", "success");
            response.put("count", users.size());
            response.put("users", users);
            
            logger.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error fetching users: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Fetching user with ID: {}", id);
            
            Optional<User> user = userRepository.findById(id);
            
            if (user.isPresent()) {
                response.put("status", "success");
                response.put("user", user.get());
                
                logger.info("User found: {}", user.get().getName());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "not_found");
                response.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error fetching user by ID: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error fetching user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Search users by name
    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Searching users by name: {}", name);
            
            List<User> users = userRepository.findByNameContainingIgnoreCase(name);
            
            response.put("status", "success");
            response.put("count", users.size());
            response.put("users", users);
            response.put("search_term", name);
            
            logger.info("Found {} users matching '{}'", users.size(), name);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching users: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error searching users: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Update user
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Updating user with ID: {}", id);
            
            Optional<User> optionalUser = userRepository.findById(id);
            
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setName(userDetails.getName());
                user.setEmail(userDetails.getEmail());
                user.setAge(userDetails.getAge());
                
                User updatedUser = userRepository.save(user);
                
                response.put("status", "success");
                response.put("message", "User updated successfully");
                response.put("user", updatedUser);
                
                logger.info("User updated: {}", updatedUser.getName());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "not_found");
                response.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error updating user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Deleting user with ID: {}", id);
            
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                
                response.put("status", "success");
                response.put("message", "User deleted successfully");
                
                logger.info("User deleted with ID: {}", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "not_found");
                response.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error deleting user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get database statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Fetching database statistics");
            
            long totalUsers = userRepository.count();
            
            response.put("status", "success");
            response.put("total_users", totalUsers);
            response.put("table_name", "users");
            
            logger.info("Database stats - Total users: {}", totalUsers);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching database stats: {}", e.getMessage());
            
            response.put("status", "error");
            response.put("message", "Error fetching stats: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
