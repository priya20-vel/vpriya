package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Lob
    @Column(name = "file_data")
    private byte[] fileData;
    
    @Lob
    @Column(name = "extracted_text")
    private String extractedText;
    
    @Lob
    @Column(name = "skills")
    private String skills;
    
    @Lob
    @Column(name = "experience")
    private String experience;
    
    @Lob
    @Column(name = "education")
    private String education;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JobApplication> applications = new HashSet<>();
    
    public Resume() {
        this.uploadDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Set<JobApplication> getApplications() { return applications; }
    public void setApplications(Set<JobApplication> applications) { this.applications = applications; }
}
