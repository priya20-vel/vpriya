package com.example.demo.service;

import com.example.demo.entity.Resume;
import com.example.demo.entity.User;
import com.example.demo.repository.ResumeRepository;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ResumeParsingService {
    
    @Autowired
    private ResumeRepository resumeRepository;
    
    private final Tika tika = new Tika();
    private final Parser parser = new AutoDetectParser();
    
    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Python", "JavaScript", "React", "Spring Boot", "Node.js", 
        "SQL", "MongoDB", "AWS", "Docker", "Kubernetes", "Git", "Jenkins",
        "Angular", "Vue.js", "TypeScript", "C++", "C#", ".NET", "PHP",
        "Machine Learning", "Data Science", "DevOps", "Agile", "Scrum"
    );
    
    public Resume parseResume(MultipartFile file) throws Exception {
        System.out.println("Starting resume parsing for: " + file.getOriginalFilename());
        System.out.println("File size: " + file.getSize() + " bytes");
        System.out.println("Content type: " + file.getContentType());
        
        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        
        try {
            String extractedText = extractTextFromFile(file);
            System.out.println("Text extraction successful, length: " + extractedText.length());
            resume.setExtractedText(extractedText);
            
            resume.setSkills(extractSkills(extractedText));
            resume.setExperience(extractExperience(extractedText));
            resume.setEducation(extractEducation(extractedText));
            
            System.out.println("Resume parsing completed successfully");
            return resume;
        } catch (Exception e) {
            System.err.println("Error parsing resume: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to parse resume: " + e.getMessage());
        }
    }
    
    public Resume saveResume(Resume resume) {
        return resumeRepository.save(resume);
    }
    
    public List<Resume> getResumesByUser(User user) {
        return resumeRepository.findByUser(user);
    }
    
    public Resume getResumeById(Long id, User user) {
        Resume resume = resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only access your own resumes");
        }
        
        return resume;
    }
    
    public void deleteResume(Long id, User user) {
        Resume resume = getResumeById(id, user);
        resumeRepository.delete(resume);
    }
    
    private String extractTextFromFile(MultipartFile file) throws Exception {
        try (InputStream stream = file.getInputStream()) {
            // Use Tika directly for simpler parsing
            String text = tika.parseToString(stream);
            return text;
        } catch (Exception e) {
            // Fallback to manual parsing if Tika fails
            try (InputStream stream = file.getInputStream()) {
                BodyContentHandler handler = new BodyContentHandler(-1);
                Metadata metadata = new Metadata();
                ParseContext context = new ParseContext();
                parser.parse(stream, handler, metadata, context);
                return handler.toString();
            }
        }
    }
    
    private String extractSkills(String text) {
        StringBuilder skills = new StringBuilder();
        text = text.toLowerCase();
        
        for (String skill : COMMON_SKILLS) {
            if (text.contains(skill.toLowerCase())) {
                if (skills.length() > 0) {
                    skills.append(", ");
                }
                skills.append(skill);
            }
        }
        
        Pattern pattern = Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\b");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String potentialSkill = matcher.group(1);
            if (potentialSkill.length() > 2 && !skills.toString().contains(potentialSkill)) {
                if (skills.length() > 0) {
                    skills.append(", ");
                }
                skills.append(potentialSkill);
            }
        }
        
        return skills.length() > 0 ? skills.toString() : "No specific skills found";
    }
    
    private String extractExperience(String text) {
        StringBuilder experience = new StringBuilder();
        
        Pattern yearPattern = Pattern.compile("(\\d+)\\s*(?:years?|yrs?)\\s*(?:of\\s*)?experience", Pattern.CASE_INSENSITIVE);
        Matcher yearMatcher = yearPattern.matcher(text);
        
        while (yearMatcher.find()) {
            if (experience.length() > 0) {
                experience.append(", ");
            }
            experience.append(yearMatcher.group(1)).append(" years");
        }
        
        Pattern companyPattern = Pattern.compile("(?:worked at|employed by|experience at)\\s+([A-Z][a-zA-Z\\s&]+)", Pattern.CASE_INSENSITIVE);
        Matcher companyMatcher = companyPattern.matcher(text);
        
        while (companyMatcher.find()) {
            if (experience.length() > 0) {
                experience.append(", ");
            }
            experience.append("Company: ").append(companyMatcher.group(1).trim());
        }
        
        return experience.length() > 0 ? experience.toString() : "Experience details not found";
    }
    
    private String extractEducation(String text) {
        StringBuilder education = new StringBuilder();
        
        List<String> educationKeywords = Arrays.asList("Bachelor", "Master", "PhD", "B.Sc", "M.Sc", "B.E", "M.E", "B.Tech", "M.Tech", "MBA", "University", "College");
        
        for (String keyword : educationKeywords) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b.*?(?:[.\\n]|$)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            while (matcher.find()) {
                String match = matcher.group().trim();
                if (match.length() > 5) {
                    if (education.length() > 0) {
                        education.append(", ");
                    }
                    education.append(match);
                }
            }
        }
        
        return education.length() > 0 ? education.toString() : "Education details not found";
    }
    
    private String extractName(String text) {
        // First, remove phone numbers from text to avoid confusion
        String textWithoutPhones = text.replaceAll("\\b(?:\\+?91[-\\s]?|0)?[6-9]\\d{9}\\b", "");
        textWithoutPhones = textWithoutPhones.replaceAll("\\b\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}\\b", "");
        textWithoutPhones = textWithoutPhones.replaceAll("\\b\\(\\d{3}\\)\\s?\\d{3}-\\d{4}\\b", "");
        textWithoutPhones = textWithoutPhones.replaceAll("\\b\\d{10}\\b", "");
        
        // Try to extract name from resume text
        // Look for common patterns like "Name:", "Resume of:", etc.
        String[] namePatterns = {
            "name[:\\s]*([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)",
            "resume of ([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)",
            "curriculum vitae[:\\s]*([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)",
            "candidate[:\\s]*([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)",
            "profile[:\\s]*([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)",
            "^([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)", // Name at beginning
            "\\b([A-Z][a-z]+ [A-Z][a-z]+(?: [A-Z][a-z]+)?)\\b" // Any proper name
        };
        
        for (String pattern : namePatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(textWithoutPhones);
            if (m.find()) {
                String name = m.group(1).trim();
                // Validate name - should contain only letters and spaces
                if (name.matches("^[A-Za-z\\s]+$") && name.length() > 2 && name.length() < 50) {
                    // Additional validation: not all uppercase or lowercase
                    if (!name.equals(name.toUpperCase()) && !name.equals(name.toLowerCase())) {
                        System.out.println("Found name: " + name);
                        return name;
                    }
                }
            }
        }
        
        // Try to find email and extract name from it
        String email = extractEmail(text);
        if (!email.equals("email@example.com")) {
            String emailName = email.split("@")[0];
            // Convert email name to proper case
            String nameFromEmail = emailName.replaceAll("[._-]", " ");
            // Capitalize first letter of each word
            String[] words = nameFromEmail.split("\\s+");
            StringBuilder capitalized = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    capitalized.append(Character.toUpperCase(word.charAt(0)))
                              .append(word.substring(1).toLowerCase())
                              .append(" ");
                }
            }
            String finalName = capitalized.toString().trim();
            if (finalName.length() > 2 && finalName.length() < 30) {
                System.out.println("Generated name from email: " + finalName);
                return finalName;
            }
        }
        
        return "Candidate Name";
    }
    
    private String extractEmail(String text) {
        // Extract email using regex
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = emailPattern.matcher(text);
        
        // Find all emails and return the first valid one
        while (matcher.find()) {
            String email = matcher.group().trim();
            // Validate email format
            if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("Found email: " + email);
                return email;
            }
        }
        
        return "email@example.com";
    }
    
    private String extractPhone(String text) {
        // Extract phone number using regex for Indian numbers
        Pattern phonePattern = Pattern.compile("\\b(?:\\+?91[-\\s]?|0)?[6-9]\\d{9}\\b");
        Matcher matcher = phonePattern.matcher(text);
        
        if (matcher.find()) {
            String phone = matcher.group().trim();
            System.out.println("Found phone: " + phone);
            return phone;
        }
        
        // Try other phone formats
        Pattern[] otherPhonePatterns = {
            Pattern.compile("\\b\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}\\b"), // XXX-XXX-XXXX
            Pattern.compile("\\b\\(\\d{3}\\)\\s?\\d{3}-\\d{4}\\b"), // (XXX) XXX-XXXX
            Pattern.compile("\\b\\d{10}\\b") // XXXXXXXXXX
        };
        
        for (Pattern pattern : otherPhonePatterns) {
            Matcher otherMatcher = pattern.matcher(text);
            if (otherMatcher.find()) {
                String phone = otherMatcher.group().trim();
                System.out.println("Found phone (other format): " + phone);
                return phone;
            }
        }
        
        return "+91-9876543210";
    }
    
    private List<String> extractExperienceList(String text) {
        List<String> experience = new ArrayList<>();
        String[] experienceKeywords = {"experience", "work history", "employment", "professional experience", "work experience", "internship", "intern"};
        
        // Look for experience sections
        for (String keyword : experienceKeywords) {
            int index = text.toLowerCase().indexOf(keyword);
            if (index != -1) {
                // Extract text after the keyword
                int start = index + keyword.length();
                int end = text.toLowerCase().indexOf("education", start);
                if (end == -1) end = text.toLowerCase().indexOf("skills", start);
                if (end == -1) end = text.length();
                
                String experienceText = text.substring(start, Math.min(start + 500, end));
                
                // Extract specific experience entries
                String[] lines = experienceText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.length() > 10 && !line.toLowerCase().contains("experience") && 
                        !line.toLowerCase().contains("employment") && !line.toLowerCase().contains("work") &&
                        !line.toLowerCase().contains("internship") && !line.toLowerCase().contains("intern")) {
                        
                        // Look for patterns like "Company Name - Position" or "Position at Company"
                        if (line.matches(".*\\bat\\b.*") || line.matches(".*\\b-\\b.*") || 
                            line.matches(".*\\d{4}.*") || line.length() > 20) {
                            experience.add(line);
                            System.out.println("Found experience: " + line);
                        }
                    }
                }
                
                if (experience.size() > 0) break; // Stop after finding first experience section
            }
        }
        
        // If no experience found, try to find work patterns including internships
        if (experience.isEmpty()) {
            Pattern[] workPatterns = {
                Pattern.compile("\\b([A-Z][a-z]+ [A-Z][a-z]+(?:\\s+Inc|Corp|Ltd|LLC)?)\\s*[-–]\\s*([A-Z][a-z].*)"),
                Pattern.compile("\\b([A-Z][a-z].*)\\s*[-–]\\s*([A-Z][a-z]+ [A-Z][a-z]+(?:\\s+Inc|Corp|Ltd|LLC)?)"),
                Pattern.compile("\\b(Intern|Internship)\\s+(?:at|as|for)\\s+([A-Z][a-z]+.*)"),
                Pattern.compile("\\b([A-Z][a-z]+.*)\\s+(?:as|for)\\s+(?:Intern|Internship)"),
                Pattern.compile("\\b([A-Z][a-z]+.*)\\s+(?:at|in)\\s+([A-Z][a-z]+.*)\\s*\\((\\d{4})\\s*-\\s*(\\d{4}|present))")
            };
            
            for (Pattern pattern : workPatterns) {
                Matcher workMatcher = pattern.matcher(text);
                while (workMatcher.find() && experience.size() < 3) {
                    String workEntry = "";
                    if (workMatcher.groupCount() >= 2) {
                        if (workMatcher.group(1).toLowerCase().contains("intern") || 
                            workMatcher.group(2).toLowerCase().contains("intern")) {
                            // Internship entry
                            workEntry = "Internship at " + workMatcher.group(2).trim();
                        } else {
                            // Regular work entry
                            workEntry = workMatcher.group(1).trim() + " at " + workMatcher.group(2).trim();
                        }
                    } else {
                        workEntry = workMatcher.group().trim();
                    }
                    experience.add(workEntry);
                    System.out.println("Found work pattern: " + workEntry);
                }
                if (experience.size() > 0) break;
            }
        }
        
        return experience;
    }
    
    private List<String> extractEducationList(String text) {
        List<String> education = new ArrayList<>();
        String[] educationKeywords = {"education", "academic", "university", "college", "degree", "bachelor", "master", "phd"};
        
        // Look for education sections
        for (String keyword : educationKeywords) {
            int index = text.toLowerCase().indexOf(keyword);
            if (index != -1) {
                // Extract text after the keyword
                int start = index + keyword.length();
                int end = text.toLowerCase().indexOf("experience", start);
                if (end == -1) end = text.toLowerCase().indexOf("skills", start);
                if (end == -1) end = text.length();
                
                String educationText = text.substring(start, Math.min(start + 500, end));
                
                // Extract specific education entries
                String[] lines = educationText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.length() > 10 && !line.toLowerCase().contains("education") && 
                        !line.toLowerCase().contains("academic") && !line.toLowerCase().contains("university")) {
                        
                        // Look for degree patterns
                        if (line.toLowerCase().matches(".*\\b(bachelor|master|phd|b\\.sc|m\\.sc|b\\.tech|m\\.tech|b\\.e|m\\.e)\\b.*") ||
                            line.toLowerCase().matches(".*\\buniversity\\b.*") ||
                            line.toLowerCase().matches(".*\\bcollege\\b.*") ||
                            line.matches(".*\\d{4}.*")) {
                            education.add(line);
                            System.out.println("Found education: " + line);
                        }
                    }
                }
                
                if (education.size() > 0) break; // Stop after finding first education section
            }
        }
        
        // If no education found, try to find degree patterns
        if (education.isEmpty()) {
            Pattern[] degreePatterns = {
                Pattern.compile("\\b(Bachelor|Master|PhD|B\\.Sc|M\\.Sc|B\\.Tech|M\\.Tech|B\\.E|M\\.E)\\s+in\\s+([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)"),
                Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)\\s+(University|College|Institute)"),
                Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)\\s+\\((\\d{4})\\s*-\\s*\\d{4})\\b")
            };
            
            for (Pattern pattern : degreePatterns) {
                Matcher degreeMatcher = pattern.matcher(text);
                while (degreeMatcher.find() && education.size() < 3) {
                    String degreeEntry = degreeMatcher.group();
                    education.add(degreeEntry);
                    System.out.println("Found degree pattern: " + degreeEntry);
                }
                if (education.size() > 0) break;
            }
        }
        
        return education;
    }
    
    public Resume getResumeById(Long resumeId) {
        Optional<Resume> resumeOpt = resumeRepository.findById(resumeId);
        return resumeOpt.orElse(null);
    }
    
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }
    
    public Map<String, Object> parseResumeText(String extractedText) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("=== RESUME PARSING START ===");
            System.out.println("Text length: " + extractedText.length());
            System.out.println("First 200 chars: " + extractedText.substring(0, Math.min(200, extractedText.length())));
            
            // Extract name
            String name = extractName(extractedText);
            result.put("name", name);
            System.out.println("Extracted name: " + name);
            
            // Extract email
            String email = extractEmail(extractedText);
            result.put("email", email);
            System.out.println("Extracted email: " + email);
            
            // Extract phone
            String phone = extractPhone(extractedText);
            result.put("phone", phone);
            System.out.println("Extracted phone: " + phone);
            
            // Extract skills
            String skills = extractSkills(extractedText);
            result.put("skills", skills);
            System.out.println("Extracted skills: " + skills);
            
            // Extract experience
            List<String> experience = extractExperienceList(extractedText);
            result.put("experience", experience);
            System.out.println("Extracted experience entries: " + experience.size());
            
            // Extract education
            List<String> education = extractEducationList(extractedText);
            result.put("education", education);
            System.out.println("Extracted education entries: " + education.size());
            
            System.out.println("=== RESUME PARSING END ===");
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to parse resume text: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    public String extractTextFromMultipartFile(MultipartFile file) throws Exception {
        try (InputStream stream = file.getInputStream()) {
            // Use Tika directly for simpler parsing
            String text = tika.parseToString(stream);
            System.out.println("Tika extracted text length: " + text.length());
            return text;
        } catch (Exception e) {
            System.err.println("Tika parsing failed: " + e.getMessage());
            // Fallback to manual parsing if Tika fails
            try (InputStream stream = file.getInputStream()) {
                BodyContentHandler handler = new BodyContentHandler(-1);
                Metadata metadata = new Metadata();
                ParseContext context = new ParseContext();
                parser.parse(stream, handler, metadata, context);
                String fallbackText = handler.toString();
                System.out.println("Fallback parsing length: " + fallbackText.length());
                return fallbackText;
            } catch (Exception fallbackException) {
                System.err.println("Fallback parsing also failed: " + fallbackException.getMessage());
                throw new Exception("Failed to extract text from PDF: " + e.getMessage());
            }
        }
    }
}
