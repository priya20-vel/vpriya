package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_percentage")
    private BigDecimal matchPercentage;
    
    @Column(name = "weighted_score")
    private BigDecimal weightedScore;
    
    @Lob
    @Column(name = "skill_gap_analysis")
    private String skillGapAnalysis;
    
    @Column(name = "is_duplicate")
    private boolean isDuplicate = false;
    
    @Column(name = "application_date")
    private LocalDateTime applicationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public JobApplication() {
        this.applicationDate = LocalDateTime.now();
    }
    
    public enum ApplicationStatus {
        PENDING, UNDER_REVIEW, SHORTLISTED, REJECTED, HIRED
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(BigDecimal matchPercentage) { this.matchPercentage = matchPercentage; }
    
    public BigDecimal getWeightedScore() { return weightedScore; }
    public void setWeightedScore(BigDecimal weightedScore) { this.weightedScore = weightedScore; }
    
    public String getSkillGapAnalysis() { return skillGapAnalysis; }
    public void setSkillGapAnalysis(String skillGapAnalysis) { this.skillGapAnalysis = skillGapAnalysis; }
    
    public boolean isDuplicate() { return isDuplicate; }
    public void setDuplicate(boolean duplicate) { isDuplicate = duplicate; }
    
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
