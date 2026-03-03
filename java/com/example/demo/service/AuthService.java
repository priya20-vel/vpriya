package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public User register(User user) {
        System.out.println("=== DEBUG: Registration attempt for ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("ERROR: Username already exists: " + user.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Simple email check - allow any registration
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            System.out.println("WARNING: Email already exists, but allowing fresh registration");
            userRepository.delete(existingUser.get());
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(true);
        
        User savedUser = userRepository.save(user);
        
        System.out.println("SUCCESS: User registered and verified: " + user.getEmail());
        System.out.println("User ID: " + savedUser.getId());
        
        return savedUser;
    }
    
    public Map<String, Object> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Auto-verify user if not verified (no OTP verification)
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            System.out.println("Auto-verified user: " + user.getEmail());
        }
        
        String token = generateToken(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", getUserInfo(user));
        response.put("message", "Login successful");
        response.put("tokenExpirationHours", jwtExpiration / (1000 * 60 * 60)); // Convert to hours
        
        return response;
    }
    
    public Map<String, Object> googleLogin(String googleId, String email, String fullName) {
        // Simplified google login - just find or create user by email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // Create new user
            user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("google123"));
            user.setRole(User.Role.JOB_SEEKER);
            user.setVerified(true);
            user = userRepository.save(user);
        }
        
        // Generate token and return
        String token = generateToken(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", getUserInfo(user));
        response.put("message", "Google login successful");
        response.put("tokenExpirationHours", jwtExpiration / (1000 * 60 * 60));
        
        return response;
    }
    
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        // OTP functionality removed - implement proper email reset if needed
        System.out.println("Password reset requested for: " + email);
        System.out.println("Implement proper email reset functionality without OTP");
    }
    
    public void resetPassword(String token, String newPassword) {
        // This is a simplified implementation
        // In production, you'd validate the reset token
        // For now, we'll use OTP as reset token
        
        throw new RuntimeException("Password reset functionality needs to be implemented with proper token validation");
    }
    
    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().toString());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public User getUserFromToken(String token) {
        try {
            System.out.println("DEBUG: Parsing token with secret: " + jwtSecret.substring(0, Math.min(10, jwtSecret.length())) + "...");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            Long userId = claims.get("userId", Long.class);
            System.out.println("DEBUG: Extracted userId from token: " + userId);
            
            if (userId == null) {
                System.out.println("ERROR: No userId found in token claims");
                return null;
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.out.println("ERROR: No user found with userId: " + userId);
                return null;
            }
            
            System.out.println("DEBUG: Successfully found user: " + user.getUsername());
            return user;
            
        } catch (ExpiredJwtException e) {
            System.err.println("ERROR: Token has expired: " + e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            System.err.println("ERROR: Token is malformed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to parse token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("role", user.getRole());
        userInfo.put("isVerified", user.isVerified());
        return userInfo;
    }
}
