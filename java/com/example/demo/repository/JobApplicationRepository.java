package com.example.demo.repository;

import com.example.demo.entity.Job;
import com.example.demo.entity.JobApplication;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJob(Job job);
    List<JobApplication> findByUser(User user);
    Optional<JobApplication> findByJobAndUser(Job job, User user);
    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
}
