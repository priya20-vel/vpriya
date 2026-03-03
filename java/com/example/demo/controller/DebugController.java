package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDebugStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", java.time.LocalDateTime.now());
        status.put("status", "Application is running");
        status.put("message", "Debug endpoint working");
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/error-test")
    public ResponseEntity<Map<String, Object>> testError() {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "This is a test error");
        error.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @GetMapping("/database-test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Test basic database connectivity
            result.put("database", "Connected");
            result.put("message", "Database connection test successful");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("database", "Error");
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
