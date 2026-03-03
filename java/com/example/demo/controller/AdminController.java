package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080"})
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody Department department, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Department created = adminService.createDepartment(department);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Department updated = adminService.updateDepartment(id, department);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            adminService.deleteDepartment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Department deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs(HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Job> jobs = adminService.getAllJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            String newRole = request.get("role");
            User updated = adminService.updateUserRole(id, newRole);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/users/{id}/verify")
    public ResponseEntity<?> verifyUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            adminService.verifyUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User verified successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> analytics = adminService.getAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return authService.getUserFromToken(token);
        }
        throw new RuntimeException("Invalid or missing token");
    }
}
