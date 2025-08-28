package com.hugenet.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find users by name containing the given string (case-insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by age greater than or equal to the given value
    List<User> findByAgeGreaterThanEqual(Integer age);
    
    // Custom query to find users by age range
    @Query("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    List<User> findUsersByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    // Count users by age
    Long countByAge(Integer age);
}
