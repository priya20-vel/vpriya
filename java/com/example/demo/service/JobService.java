package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.JobApplication;
import com.example.demo.entity.Resume;
import com.example.demo.entity.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.JobApplicationRepository;
import com.example.demo.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private JobApplicationRepository applicationRepository;
    
    @Autowired
    private ResumeRepository resumeRepository;
    
    @Autowired
    private AIScreeningService aiScreeningService;
    
    public List<Department> getAllDepartments() {
        try {
            List<Department> departments = departmentRepository.findAll();
            System.out.println("Found " + departments.size() + " departments");
            return departments;
        } catch (Exception e) {
            System.err.println("Error getting all departments: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get departments: " + e.getMessage());
        }
    }
    
    public List<Job> getJobsByDepartment(Long departmentId) {
        try {
            if (departmentId == null || departmentId <= 0) {
                throw new IllegalArgumentException("Invalid department ID");
            }
            
            Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + departmentId));
            
            List<Job> jobs = jobRepository.findByDepartmentAndIsActive(department, true);
            System.out.println("Found " + jobs.size() + " jobs for department: " + department.getName());
            return jobs;
        } catch (Exception e) {
            System.err.println("Error getting jobs by department: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get jobs for department: " + e.getMessage());
        }
    }
    
    public List<Job> getAllActiveJobs() {
        try {
            System.out.println("=== DEBUG: Getting all active jobs ===");
            List<Job> jobs = jobRepository.findByIsActive(true);
            System.out.println("DEBUG: Found " + jobs.size() + " active jobs");
            
            // Print job details for debugging
            for (int i = 0; i < jobs.size(); i++) {
                Job job = jobs.get(i);
                System.out.println("DEBUG: Job " + (i+1) + ": " + job.getTitle() + 
                                 " at " + job.getCompanyName());
            }
            
            return jobs;
        } catch (Exception e) {
            System.err.println("ERROR: Error getting all active jobs: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get active jobs: " + e.getMessage());
        }
    }
    
    public Job getJobById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid job ID");
            }
            
            Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
            
            System.out.println("Found job: " + job.getTitle());
            return job;
        } catch (Exception e) {
            System.err.println("Error getting job by ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get job: " + e.getMessage());
        }
    }
    
    public Job createJob(Job job, User postedBy) {
        if (postedBy.getRole() != User.Role.RECRUITER && postedBy.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only recruiters and admins can post jobs");
        }
        
        Department department = departmentRepository.findById(job.getDepartment().getId())
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        job.setDepartment(department);
        job.setPostedBy(postedBy);
        
        return jobRepository.save(job);
    }
    
    public Job updateJob(Long id, Job jobDetails, User currentUser) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getPostedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You can only update your own jobs");
        }
        
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setRequirements(jobDetails.getRequirements());
        job.setSkills(jobDetails.getSkills());
        job.setMinExperience(jobDetails.getMinExperience());
        job.setMaxExperience(jobDetails.getMaxExperience());
        job.setMinSalary(jobDetails.getMinSalary());
        job.setMaxSalary(jobDetails.getMaxSalary());
        job.setJobType(jobDetails.getJobType());
        job.setWorkLocation(jobDetails.getWorkLocation());
        job.setDeadline(jobDetails.getDeadline());
        
        if (jobDetails.getDepartment() != null) {
            Department department = departmentRepository.findById(jobDetails.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
            job.setDepartment(department);
        }
        
        return jobRepository.save(job);
    }
    
    public void deleteJob(Long id, User currentUser) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getPostedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You can only delete your own jobs");
        }
        
        jobRepository.delete(job);
    }
    
    public JobApplication applyForJob(Long jobId, Long resumeId, User user) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new RuntimeException("Resume not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only use your own resume");
        }
        
        if (!job.isActive()) {
            throw new RuntimeException("Job is no longer active");
        }
        
        boolean alreadyApplied = applicationRepository.findByJobAndUser(job, user).isPresent();
        if (alreadyApplied) {
            throw new RuntimeException("You have already applied for this job");
        }
        
        JobApplication application = aiScreeningService.screenResume(resume, job);
        application.setUser(user);
        
        return applicationRepository.save(application);
    }
    
    public List<JobApplication> getJobApplications(Long jobId, User currentUser) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getPostedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You can only view applications for your own jobs");
        }
        
        return applicationRepository.findByJob(job);
    }
    
    public List<Job> searchJobs(String keyword, String department, String location) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Use the custom query method for better search
            return jobRepository.searchJobsByKeyword(keyword);
        }
        
        if (department != null && !department.trim().isEmpty()) {
            return jobRepository.findByDepartmentName(department);
        }
        
        if (location != null && !location.trim().isEmpty()) {
            return jobRepository.findByWorkLocationContaining(location);
        }
        
        return getAllActiveJobs();
    }
    
    public List<Job> getRecommendedJobs(Long userId) {
        User user = resumeRepository.findById(userId)
            .map(Resume::getUser)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Resume> resumes = resumeRepository.findByUser(user);
        if (resumes.isEmpty()) {
            return getAllActiveJobs();
        }
        
        Resume latestResume = resumes.get(resumes.size() - 1);
        return jobRepository.findRecommendedJobsBySkills(latestResume.getSkills());
    }
    
    public List<JobApplication> rankCandidates(Long jobId, User currentUser) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getPostedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You can only rank candidates for your own jobs");
        }
        
        List<JobApplication> applications = applicationRepository.findByJob(job);
        return aiScreeningService.rankCandidates(applications);
    }
}
