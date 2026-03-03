package com.example.demo.test;

// This is a simple compilation test file
// It imports all major classes to verify compilation works

import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.repository.*;
import com.example.demo.controller.*;
import com.example.demo.config.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompilationTest {
    public static void main(String[] args) {
        System.out.println("Compilation test successful!");
        System.out.println("All imports are working correctly.");
        
        // Test entity instantiation
        User user = new User();
        Job job = new Job();
        Resume resume = new Resume();
        JobApplication application = new JobApplication();
        Department department = new Department();
        
        System.out.println("Entity classes compiled successfully!");
        
        // Test enum values
        User.Role role = User.Role.JOB_SEEKER;
        Job.JobType jobType = Job.JobType.FULL_TIME;
        JobApplication.ApplicationStatus status = JobApplication.ApplicationStatus.PENDING;
        
        System.out.println("Enums compiled successfully!");
        System.out.println("AI Resume Screening System is ready to run!");
    }
}
