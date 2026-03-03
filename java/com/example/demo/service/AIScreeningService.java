package com.example.demo.service;

import com.example.demo.entity.Job;
import com.example.demo.entity.JobApplication;
import com.example.demo.entity.Resume;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIScreeningService {
    
    private final JaccardSimilarity similarityCalculator = new JaccardSimilarity();
    
    public JobApplication screenResume(Resume resume, Job job) {
        JobApplication application = new JobApplication();
        application.setResume(resume);
        application.setJob(job);
        
        BigDecimal matchPercentage = calculateMatchPercentage(resume, job);
        application.setMatchPercentage(matchPercentage);
        
        BigDecimal weightedScore = calculateWeightedScore(resume, job);
        application.setWeightedScore(weightedScore);
        
        String skillGapAnalysis = analyzeSkillGap(resume, job);
        application.setSkillGapAnalysis(skillGapAnalysis);
        
        application.setDuplicate(checkDuplicate(resume, job));
        
        return application;
    }
    
    private BigDecimal calculateMatchPercentage(Resume resume, Job job) {
        double skillMatch = calculateSkillMatch(resume.getSkills(), job.getSkills());
        double experienceMatch = calculateExperienceMatch(resume.getExperience(), job.getMinExperience(), job.getMaxExperience());
        double educationMatch = calculateEducationMatch(resume.getEducation(), job.getRequirements());
        
        double overallMatch = (skillMatch * 0.5) + (experienceMatch * 0.3) + (educationMatch * 0.2);
        
        return BigDecimal.valueOf(overallMatch * 100).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateWeightedScore(Resume resume, Job job) {
        double skillScore = calculateSkillMatch(resume.getSkills(), job.getSkills()) * 50;
        double experienceScore = calculateExperienceMatch(resume.getExperience(), job.getMinExperience(), job.getMaxExperience()) * 30;
        double educationScore = calculateEducationMatch(resume.getEducation(), job.getRequirements()) * 20;
        
        double totalScore = skillScore + experienceScore + educationScore;
        
        return BigDecimal.valueOf(totalScore).setScale(2, RoundingMode.HALF_UP);
    }
    
    private double calculateSkillMatch(String resumeSkills, String jobSkills) {
        if (resumeSkills == null || jobSkills == null) return 0.0;
        
        Set<String> resumeSkillSet = Arrays.stream(resumeSkills.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
        
        Set<String> jobSkillSet = Arrays.stream(jobSkills.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
        
        if (jobSkillSet.isEmpty()) return 0.0;
        
        long matchingSkills = resumeSkillSet.stream()
            .filter(jobSkillSet::contains)
            .count();
        
        return (double) matchingSkills / jobSkillSet.size();
    }
    
    private double calculateExperienceMatch(String resumeExperience, Integer minExp, Integer maxExp) {
        if (resumeExperience == null || minExp == null) return 0.0;
        
        int extractedYears = extractYearsFromExperience(resumeExperience);
        
        if (extractedYears >= minExp && (maxExp == null || extractedYears <= maxExp)) {
            return 1.0;
        } else if (extractedYears < minExp) {
            return Math.max(0, (double) extractedYears / minExp);
        } else {
            return Math.max(0, 1.0 - ((double) (extractedYears - maxExp) / maxExp));
        }
    }
    
    private double calculateEducationMatch(String resumeEducation, String jobRequirements) {
        if (resumeEducation == null || jobRequirements == null) return 0.0;
        
        String resumeText = resumeEducation.toLowerCase();
        String requirementsText = jobRequirements.toLowerCase();
        
        return similarityCalculator.apply(resumeText, requirementsText);
    }
    
    private String analyzeSkillGap(Resume resume, Job job) {
        if (resume.getSkills() == null || job.getSkills() == null) {
            return "Unable to analyze skill gap due to missing information";
        }
        
        Set<String> resumeSkillSet = Arrays.stream(resume.getSkills().split(","))
            .map(String::trim)
            .collect(Collectors.toSet());
        
        Set<String> jobSkillSet = Arrays.stream(job.getSkills().split(","))
            .map(String::trim)
            .collect(Collectors.toSet());
        
        Set<String> missingSkills = jobSkillSet.stream()
            .filter(skill -> !resumeSkillSet.contains(skill))
            .collect(Collectors.toSet());
        
        Set<String> extraSkills = resumeSkillSet.stream()
            .filter(skill -> !jobSkillSet.contains(skill))
            .collect(Collectors.toSet());
        
        StringBuilder analysis = new StringBuilder();
        
        if (missingSkills.isEmpty()) {
            analysis.append("All required skills are present. ");
        } else {
            analysis.append("Missing skills: ").append(String.join(", ", missingSkills)).append(". ");
        }
        
        if (extraSkills.isEmpty()) {
            analysis.append("No additional skills beyond requirements.");
        } else {
            analysis.append("Additional skills: ").append(String.join(", ", extraSkills));
        }
        
        return analysis.toString();
    }
    
    private boolean checkDuplicate(Resume resume, Job job) {
        return false;
    }
    
    private int extractYearsFromExperience(String experienceText) {
        if (experienceText == null) return 0;
        
        Pattern pattern = Pattern.compile("(\\d+)\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(experienceText);
        
        int maxYears = 0;
        while (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                maxYears = Math.max(maxYears, years);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
        
        return maxYears;
    }
    
    public List<JobApplication> rankCandidates(List<JobApplication> applications) {
        return applications.stream()
            .sorted((a1, a2) -> a2.getWeightedScore().compareTo(a1.getWeightedScore()))
            .collect(Collectors.toList());
    }
}
