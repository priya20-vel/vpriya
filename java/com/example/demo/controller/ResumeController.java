package com.example.demo.controller;

import com.example.demo.entity.Resume;
import com.example.demo.entity.User;
import com.example.demo.service.ResumeParsingService;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080"})
public class ResumeController {
    
    @Autowired
    private ResumeParsingService resumeParsingService;
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            System.out.println("=== RESUME UPLOAD DEBUG START ===");
            System.out.println("File received: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");
            System.out.println("Content type: " + file.getContentType());
            System.out.println("Request headers: " + request.getHeaderNames());
            
            // Check authorization header
            String authHeader = request.getHeader("Authorization");
            System.out.println("Auth header: " + authHeader);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("ERROR: No valid Authorization header found");
                return ResponseEntity.status(401).body(Map.of("error", "No valid token provided"));
            }
            
            // Get current user with detailed logging
            User currentUser = null;
            try {
                currentUser = getCurrentUser(request);
                System.out.println("SUCCESS: Current user found: " + currentUser.getUsername());
                System.out.println("User ID: " + currentUser.getId());
                System.out.println("User Email: " + currentUser.getEmail());
            } catch (Exception e) {
                System.err.println("ERROR: Failed to get current user: " + e.getMessage());
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token: " + e.getMessage()));
            }
            
            // Basic validation
            if (file.isEmpty()) {
                System.out.println("ERROR: File is empty");
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            
            // Create resume with actual text extraction
            System.out.println("Creating resume object...");
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setFileType(file.getContentType());
            resume.setFileSize(file.getSize());
            resume.setFileData(file.getBytes());
            resume.setContentType(file.getContentType());
            
            // Extract text from PDF
            String extractedText = "";
            try {
                extractedText = resumeParsingService.extractTextFromMultipartFile(file);
                System.out.println("Text extracted successfully, length: " + extractedText.length());
                if (extractedText.length() > 500) {
                    System.out.println("First 500 chars: " + extractedText.substring(0, 500));
                }
            } catch (Exception e) {
                System.err.println("Failed to extract text: " + e.getMessage());
                extractedText = "Resume uploaded: " + file.getOriginalFilename();
            }
            
            resume.setExtractedText(extractedText);
            
            // Parse the extracted text for structured data
            Map<String, Object> parsedData = resumeParsingService.parseResumeText(extractedText);
            
            // Set parsed data to resume fields
            resume.setSkills(parsedData.get("skills") != null ? parsedData.get("skills").toString() : "Skills not found");
            resume.setExperience(parsedData.get("experience") != null ? parsedData.get("experience").toString() : "Experience not found");
            resume.setEducation(parsedData.get("education") != null ? parsedData.get("education").toString() : "Education not found");
            
            resume.setUser(currentUser);
            
            System.out.println("Saving resume to database...");
            Resume savedResume = resumeParsingService.saveResume(resume);
            
            System.out.println("SUCCESS: Resume saved with ID: " + savedResume.getId());
            System.out.println("=== RESUME UPLOAD DEBUG END ===");
            
            // Calculate AI score
            double aiScore = calculateShortlistingScore(parsedData);
            
            System.out.println("AI Score calculated: " + aiScore);
            System.out.println("Parsed data keys: " + parsedData.keySet());
            
            return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded successfully",
                "resumeId", savedResume.getId(),
                "filename", file.getOriginalFilename(),
                "userId", currentUser.getId(),
                "parsedData", parsedData,
                "aiScore", Math.round(aiScore),
                "extractedTextLength", extractedText.length()
            ));
            
        } catch (Exception e) {
            System.err.println("ERROR: Resume upload failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Upload failed: " + e.getMessage(),
                "details", e.getClass().getSimpleName()
            ));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyResumes(HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            List<Resume> resumes = resumeParsingService.getResumesByUser(currentUser);
            return ResponseEntity.ok(resumes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getResumeById(@PathVariable Long id, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            Resume resume = resumeParsingService.getResumeById(id, currentUser);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            resumeParsingService.deleteResume(id, currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Resume deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/shortlist")
    public ResponseEntity<?> getAllResumesForShortlisting() {
        try {
            System.out.println("=== GET ALL RESUMES FOR SHORTLISTING ===");
            
            List<Resume> resumes = resumeParsingService.getAllResumes();
            System.out.println("DEBUG: Total resumes found: " + resumes.size());
            
            List<Map<String, Object>> shortlistData = new ArrayList<>();
            
            for (Resume resume : resumes) {
                Map<String, Object> resumeData = new HashMap<>();
                resumeData.put("id", resume.getId());
                resumeData.put("fileName", resume.getFileName());
                resumeData.put("uploadDate", resume.getUploadDate());
                resumeData.put("fileSize", resume.getFileSize());
                resumeData.put("contentType", resume.getContentType());
                
                // Parse resume content for shortlisting
                try {
                    Map<String, Object> parsedData = resumeParsingService.parseResumeText(resume.getExtractedText());
                    
                    // Calculate AI score
                    double aiScore = calculateShortlistingScore(parsedData);
                    
                    // Extract key information for shortlisting
                    resumeData.put("name", parsedData.get("name"));
                    resumeData.put("email", parsedData.get("email"));
                    resumeData.put("phone", parsedData.get("phone"));
                    resumeData.put("skills", parsedData.get("skills"));
                    resumeData.put("experience", parsedData.get("experience"));
                    resumeData.put("education", parsedData.get("education"));
                    resumeData.put("score", aiScore);
                    
                    System.out.println("DEBUG: Processed resume - " + resume.getFileName() + 
                                     " (Score: " + aiScore + ")");
                    
                } catch (Exception e) {
                    System.out.println("WARNING: Failed to parse resume " + resume.getFileName() + ": " + e.getMessage());
                    resumeData.put("name", "Unknown");
                    resumeData.put("email", "Unknown");
                    resumeData.put("phone", "Unknown");
                    resumeData.put("skills", new ArrayList<>());
                    resumeData.put("experience", new ArrayList<>());
                    resumeData.put("education", new ArrayList<>());
                    resumeData.put("score", 0);
                }
                
                shortlistData.add(resumeData);
            }
            
            // Sort by score (highest first)
            shortlistData.sort((a, b) -> {
                Double scoreA = (Double) a.get("score");
                Double scoreB = (Double) b.get("score");
                return scoreB.compareTo(scoreA);
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("resumes", shortlistData);
            response.put("total", shortlistData.size());
            response.put("message", "Resumes retrieved successfully for shortlisting");
            
            System.out.println("=== SHORTLISTING SUCCESS ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to get resumes for shortlisting: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get resumes: " + e.getMessage()));
        }
    }
    
    @PostMapping("/shortlist/{resumeId}")
    public ResponseEntity<?> shortlistResume(@PathVariable Long resumeId, HttpServletRequest request) {
        try {
            System.out.println("=== SHORTLIST RESUME ===");
            System.out.println("Resume ID: " + resumeId);
            
            User currentUser = getCurrentUser(request);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            Resume resume = resumeParsingService.getResumeById(resumeId);
            if (resume == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Resume not found"));
            }
            
            // Mark resume as shortlisted (you could add a shortlisted field to Resume entity)
            System.out.println("DEBUG: Resume shortlisted by user: " + currentUser.getUsername());
            System.out.println("DEBUG: Resume file: " + resume.getFileName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Resume shortlisted successfully");
            response.put("resumeId", resumeId);
            response.put("fileName", resume.getFileName());
            response.put("shortlistedBy", currentUser.getUsername());
            response.put("shortlistedDate", new java.util.Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to shortlist resume: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to shortlist resume: " + e.getMessage()));
        }
    }
    
    @GetMapping("/shortlisted")
    public ResponseEntity<?> getShortlistedResumes(HttpServletRequest request) {
        try {
            System.out.println("=== GET SHORTLISTED RESUMES ===");
            
            User currentUser = getCurrentUser(request);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            // For now, return all resumes (you could filter by shortlisted status)
            List<Resume> resumes = resumeParsingService.getAllResumes();
            
            Map<String, Object> response = new HashMap<>();
            response.put("resumes", resumes);
            response.put("total", resumes.size());
            response.put("message", "Shortlisted resumes retrieved successfully");
            response.put("user", currentUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to get shortlisted resumes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get shortlisted resumes: " + e.getMessage()));
        }
    }
    
    private double calculateShortlistingScore(Map<String, Object> parsedData) {
        double score = 0.0;
        
        try {
            System.out.println("=== AI SCORE CALCULATION START ===");
            System.out.println("Parsed data keys: " + parsedData.keySet());
            
            // Skills score (40% weight)
            Object skillsObj = parsedData.get("skills");
            if (skillsObj != null) {
                String[] skills = {};
                if (skillsObj instanceof String) {
                    String skillsStr = (String) skillsObj;
                    skills = skillsStr.split(",");
                    System.out.println("Skills as string: " + skillsStr);
                } else if (skillsObj instanceof List) {
                    List<String> skillsList = (List<String>) skillsObj;
                    skills = skillsList.toArray(new String[0]);
                    System.out.println("Skills as list: " + skillsList);
                }
                
                // High-demand skills get more points
                String[] highDemandSkills = {"java", "python", "javascript", "react", "spring", 
                                          "aws", "docker", "kubernetes", "sql", "machine learning"};
                
                for (String skill : skills) {
                    String skillLower = skill.trim().toLowerCase();
                    if (skillLower.length() > 0) {
                        if (Arrays.asList(highDemandSkills).contains(skillLower)) {
                            score += 10; // High-demand skill
                            System.out.println("High-demand skill found: " + skill + " (+10)");
                        } else {
                            score += 5; // Regular skill
                            System.out.println("Regular skill found: " + skill + " (+5)");
                        }
                    }
                }
            } else {
                System.out.println("No skills found in parsed data");
            }
            
            // Experience score (30% weight)
            Object experienceObj = parsedData.get("experience");
            if (experienceObj != null) {
                List<String> experience = new ArrayList<>();
                if (experienceObj instanceof List) {
                    experience = (List<String>) experienceObj;
                } else if (experienceObj instanceof String) {
                    experience.add((String) experienceObj);
                }
                
                if (!experience.isEmpty()) {
                    int experiencePoints = 0;
                    boolean hasInternship = false;
                    
                    for (String exp : experience) {
                        String expLower = exp.toLowerCase();
                        
                        // Check if it's an internship
                        if (expLower.contains("intern") || expLower.contains("trainee") || expLower.contains("fresher")) {
                            experiencePoints += 10; // Internship gets 10 points
                            hasInternship = true;
                            System.out.println("Internship found: " + exp + " (+10)");
                        } else {
                            experiencePoints += 15; // Regular experience gets 15 points
                            System.out.println("Regular experience found: " + exp + " (+15)");
                        }
                        
                        // Bonus for years of experience
                        if (expLower.contains("year") || expLower.matches(".*\\d+\\s+years?.*")) {
                            experiencePoints += 10; // Bonus for yearly experience
                            System.out.println("Years of experience found: " + exp + " (+10)");
                        }
                    }
                    
                    score += experiencePoints;
                    System.out.println("Total experience points: " + experiencePoints);
                    
                    // Bonus for having both internship and regular experience
                    if (hasInternship && experience.size() > 1) {
                        score += 5; // Diversity bonus
                        System.out.println("Experience diversity bonus: +5");
                    }
                }
            } else {
                System.out.println("No experience found in parsed data");
            }
            
            // Education score (20% weight)
            Object educationObj = parsedData.get("education");
            if (educationObj != null) {
                List<String> education = new ArrayList<>();
                if (educationObj instanceof List) {
                    education = (List<String>) educationObj;
                } else if (educationObj instanceof String) {
                    education.add((String) educationObj);
                }
                
                if (!education.isEmpty()) {
                    score += education.size() * 10; // 10 points per education
                    System.out.println("Education entries: " + education.size() + " (*10 = " + (education.size() * 10) + ")");
                    
                    // Bonus for higher education
                    for (String edu : education) {
                        String eduLower = edu.toLowerCase();
                        if (eduLower.contains("master") || eduLower.contains("m.sc") || eduLower.contains("m.tech")) {
                            score += 15; // Master's degree bonus
                            System.out.println("Master's degree found: " + edu + " (+15)");
                        } else if (eduLower.contains("phd") || eduLower.contains("doctorate")) {
                            score += 25; // PhD bonus
                            System.out.println("PhD found: " + edu + " (+25)");
                        } else if (eduLower.contains("bachelor") || eduLower.contains("b.sc") || eduLower.contains("b.tech") || eduLower.contains("b.e")) {
                            score += 10; // Bachelor's degree
                            System.out.println("Bachelor's degree found: " + edu + " (+10)");
                        }
                    }
                }
            } else {
                System.out.println("No education found in parsed data");
            }
            
            // Contact info completeness (10% weight)
            String email = (String) parsedData.get("email");
            String phone = (String) parsedData.get("phone");
            
            if (email != null && !email.equals("email@example.com") && !email.isEmpty()) {
                score += 5; // Email bonus
                System.out.println("Valid email found: " + email + " (+5)");
            }
            
            if (phone != null && !phone.equals("+91-9876543210") && !phone.isEmpty()) {
                score += 5; // Phone bonus
                System.out.println("Valid phone found: " + phone + " (+5)");
            }
            
            // Name completeness bonus
            String name = (String) parsedData.get("name");
            if (name != null && !name.equals("Candidate Name") && !name.isEmpty()) {
                score += 5; // Name bonus
                System.out.println("Valid name found: " + name + " (+5)");
            }
            
            // Cap the score at 100
            score = Math.min(score, 100);
            
            System.out.println("=== AI SCORE CALCULATION END ===");
            System.out.println("Final Score: " + score + "/100");
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to calculate score: " + e.getMessage());
            e.printStackTrace();
            score = 0.0;
        }
        
        return score;
    }
    
    private User getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            System.out.println("DEBUG: Auth header received: " + authHeader);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("ERROR: No Bearer token found in header");
                throw new RuntimeException("No valid token provided");
            }
            
            String token = authHeader.substring(7);
            System.out.println("DEBUG: Extracted token: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            User user = authService.getUserFromToken(token);
            if (user == null) {
                System.out.println("ERROR: authService.getUserFromToken returned null");
                throw new RuntimeException("User not found for token");
            }
            
            System.out.println("DEBUG: Successfully retrieved user: " + user.getUsername());
            System.out.println("DEBUG: User ID: " + user.getId());
            System.out.println("DEBUG: User Email: " + user.getEmail());
            
            return user;
            
        } catch (Exception e) {
            System.err.println("ERROR: getCurrentUser failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
}
