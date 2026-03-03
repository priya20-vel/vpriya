package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.JobService;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.DepartmentRepository;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/runtime-test")
public class RuntimeTestController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @GetMapping("/dependencies")
    public ResponseEntity<Map<String, Object>> testDependencies() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test autowired dependencies
            result.put("jobService", jobService != null ? "OK" : "NULL");
            result.put("jobRepository", jobRepository != null ? "OK" : "NULL");
            result.put("departmentRepository", departmentRepository != null ? "OK" : "NULL");
            
            // Test basic repository operations
            long jobCount = jobRepository.count();
            long deptCount = departmentRepository.count();
            
            result.put("jobCount", jobCount);
            result.put("departmentCount", deptCount);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            result.put("stackTrace", getStackTrace(e));
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/service-methods")
    public ResponseEntity<Map<String, Object>> testServiceMethods() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test getAllDepartments
            var departments = jobService.getAllDepartments();
            result.put("departments", departments.size() + " found");
            
            // Test getAllActiveJobs
            var jobs = jobService.getAllActiveJobs();
            result.put("activeJobs", jobs.size() + " found");
            
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            result.put("stackTrace", getStackTrace(e));
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/null-pointer-test")
    public ResponseEntity<Map<String, Object>> testNullPointer() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // This should work if dependencies are properly injected
            String jobServiceStatus = jobService.getClass().getSimpleName();
            String jobRepoStatus = jobRepository.getClass().getSimpleName();
            
            result.put("jobServiceClass", jobServiceStatus);
            result.put("jobRepositoryClass", jobRepoStatus);
            result.put("status", "SUCCESS");
            
        } catch (NullPointerException e) {
            result.put("status", "NULL_POINTER_ERROR");
            result.put("error", e.getMessage());
            result.put("stackTrace", getStackTrace(e));
        } catch (Exception e) {
            result.put("status", "OTHER_ERROR");
            result.put("error", e.getMessage());
            result.put("stackTrace", getStackTrace(e));
        }
        
        return ResponseEntity.ok(result);
    }
    
    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 1000) break; // Limit stack trace length
        }
        return sb.toString();
    }
}
