package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    long countByRole(User.Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true")
    long countByIsVerified(boolean isVerified);
    
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();
    
    @Query(value = "SELECT DATE(u.created_at) as date, COUNT(u.id) as count FROM users u " +
                   "WHERE u.created_at >= CURRENT_DATE - INTERVAL ?1 DAY " +
                   "GROUP BY DATE(u.created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> countUsersByLastNDays(int days);
}
