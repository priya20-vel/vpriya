package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalJobs = jobRepository.count();
        long totalDepartments = departmentRepository.count();
        long activeJobs = jobRepository.countByIsActive(true);
        long verifiedUsers = userRepository.countByIsVerified(true);
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalJobs", totalJobs);
        stats.put("totalDepartments", totalDepartments);
        stats.put("activeJobs", activeJobs);
        stats.put("verifiedUsers", verifiedUsers);
        stats.put("unverifiedUsers", totalUsers - verifiedUsers);
        
        // Role distribution
        long jobSeekers = userRepository.countByRole(User.Role.JOB_SEEKER);
        long recruiters = userRepository.countByRole(User.Role.RECRUITER);
        long admins = userRepository.countByRole(User.Role.ADMIN);
        
        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("jobSeekers", jobSeekers);
        roleDistribution.put("recruiters", recruiters);
        roleDistribution.put("admins", admins);
        stats.put("roleDistribution", roleDistribution);
        
        return stats;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Department createDepartment(Department department) {
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            throw new RuntimeException("Department with this name already exists");
        }
        return departmentRepository.save(department);
    }
    
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        department.setRequiredSkills(departmentDetails.getRequiredSkills());
        
        return departmentRepository.save(department);
    }
    
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        // Check if department has jobs
        List<Job> jobs = jobRepository.findByDepartment(department);
        if (!jobs.isEmpty()) {
            throw new RuntimeException("Cannot delete department with existing jobs");
        }
        
        departmentRepository.delete(department);
    }
    
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    
    public User updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            User.Role role = User.Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be JOB_SEEKER, RECRUITER, or ADMIN");
        }
        
        return userRepository.save(user);
    }
    
    public void verifyUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setVerified(true);
        userRepository.save(user);
    }
    
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Job statistics by department
        List<Object[]> jobStatsByDepartment = jobRepository.countJobsByDepartment();
        Map<String, Long> jobsByDepartment = new HashMap<>();
        for (Object[] stat : jobStatsByDepartment) {
            jobsByDepartment.put((String) stat[0], (Long) stat[1]);
        }
        analytics.put("jobsByDepartment", jobsByDepartment);
        
        // User registration trends (last 7 days)
        List<Object[]> userRegistrationTrend = userRepository.countUsersByLastNDays(7);
        Map<String, Long> registrationTrend = new HashMap<>();
        for (Object[] stat : userRegistrationTrend) {
            registrationTrend.put((String) stat[0], (Long) stat[1]);
        }
        analytics.put("userRegistrationTrend", registrationTrend);
        
        // Most active recruiters
        List<Object[]> topRecruiters = jobRepository.findTopRecruiters();
        analytics.put("topRecruiters", topRecruiters);
        
        // Job applications statistics
        long totalApplications = jobRepository.countTotalApplications();
        long pendingApplications = jobRepository.countPendingApplications();
        long shortlistedApplications = jobRepository.countShortlistedApplications();
        
        Map<String, Long> applicationStats = new HashMap<>();
        applicationStats.put("total", totalApplications);
        applicationStats.put("pending", pendingApplications);
        applicationStats.put("shortlisted", shortlistedApplications);
        analytics.put("applicationStats", applicationStats);
        
        return analytics;
    }
}
