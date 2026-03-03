package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.JobApplication;
import com.example.demo.entity.User;
import com.example.demo.service.JobService;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080"})
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        try {
            List<Department> departments = jobService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            System.err.println("Error getting departments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Job>> getJobsByDepartment(@PathVariable Long departmentId) {
        try {
            if (departmentId == null || departmentId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            List<Job> jobs = jobService.getJobsByDepartment(departmentId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            System.err.println("Error getting jobs by department: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Job>> getAllJobs() {
        try {
            System.out.println("=== DEBUG: JobController.getAllJobs() called ===");
            List<Job> jobs = jobService.getAllActiveJobs();
            System.out.println("DEBUG: JobController returning " + jobs.size() + " jobs");
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            System.err.println("ERROR: JobController error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/debug/count")
    public ResponseEntity<Map<String, Object>> debugJobCount() {
        try {
            Map<String, Object> response = new HashMap<>();
            List<Job> allJobs = jobService.getAllActiveJobs();
            List<Job> activeJobs = allJobs.stream().filter(Job::isActive).toList();
            
            response.put("totalJobs", allJobs.size());
            response.put("activeJobs", activeJobs.size());
            response.put("allJobs", allJobs.size());
            response.put("activeJobList", activeJobs.stream()
                .map(job -> Map.of(
                    "id", job.getId(),
                    "title", job.getTitle(),
                    "company", job.getCompanyName(),
                    "active", job.isActive()
                )).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("stackTrace", e.toString());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            System.err.println("Error getting job by id: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createJob(@RequestBody Job job, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getRole() != User.Role.RECRUITER && currentUser.getRole() != User.Role.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only recruiters and admins can post jobs");
                return ResponseEntity.badRequest().body(error);
            }
            
            Job createdJob = jobService.createJob(job, currentUser);
            return ResponseEntity.ok(createdJob);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job job, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            Job updatedJob = jobService.updateJob(id, job, currentUser);
            return ResponseEntity.ok(updatedJob);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            jobService.deleteJob(id, currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Job deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{jobId}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long jobId, @RequestParam Long resumeId, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            JobApplication application = jobService.applyForJob(jobId, resumeId, currentUser);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{jobId}/applications")
    public ResponseEntity<List<JobApplication>> getJobApplications(@PathVariable Long jobId, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            List<JobApplication> applications = jobService.getJobApplications(jobId, currentUser);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String department,
                                               @RequestParam(required = false) String location) {
        List<Job> jobs = jobService.searchJobs(keyword, department, location);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<List<Job>> getRecommendedJobs(@PathVariable Long userId, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            if (currentUser.getId() != userId && currentUser.getRole() != User.Role.ADMIN) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Job> recommendedJobs = jobService.getRecommendedJobs(userId);
            return ResponseEntity.ok(recommendedJobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
