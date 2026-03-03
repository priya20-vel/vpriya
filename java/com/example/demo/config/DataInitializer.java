package com.example.demo.config;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.User;
import com.example.demo.entity.User.Role;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            JobRepository jobRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Create sample departments
            if (departmentRepository.count() == 0) {
                Department itDepartment = new Department();
                itDepartment.setName("Information Technology");
                itDepartment.setDescription("Software development and IT infrastructure");
                itDepartment.setRequiredSkills("Java, Python, JavaScript, SQL, Cloud Computing");
                departmentRepository.save(itDepartment);
                
                Department hrDepartment = new Department();
                hrDepartment.setName("Human Resources");
                hrDepartment.setDescription("Recruitment and employee management");
                hrDepartment.setRequiredSkills("Communication, Recruitment, HR Policies, Employee Relations");
                departmentRepository.save(hrDepartment);
                
                Department financeDepartment = new Department();
                financeDepartment.setName("Finance");
                financeDepartment.setDescription("Financial planning and analysis");
                financeDepartment.setRequiredSkills("Accounting, Financial Analysis, Excel, Reporting, Budgeting");
                departmentRepository.save(financeDepartment);
                
                System.out.println("Sample departments created!");
            }
            
            // Create sample users
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setFullName("Admin User");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setVerified(true);
                userRepository.save(admin);
                
                User recruiter = new User();
                recruiter.setUsername("recruiter");
                recruiter.setEmail("recruiter@example.com");
                recruiter.setFullName("Recruiter User");
                recruiter.setPassword(passwordEncoder.encode("recruiter123"));
                recruiter.setRole(Role.RECRUITER);
                recruiter.setVerified(true);
                userRepository.save(recruiter);
                
                User jobSeeker = new User();
                jobSeeker.setUsername("jobseeker");
                jobSeeker.setEmail("jobseeker@example.com");
                jobSeeker.setFullName("Job Seeker");
                jobSeeker.setPassword(passwordEncoder.encode("seeker123"));
                jobSeeker.setRole(Role.JOB_SEEKER);
                jobSeeker.setVerified(true);
                userRepository.save(jobSeeker);
                
                System.out.println("Sample users created!");
                System.out.println("Admin: admin/admin123");
                System.out.println("Recruiter: recruiter/recruiter123");
                System.out.println("Job Seeker: jobseeker/seeker123");
            }
            
            // Create sample jobs - FORCE CREATE EVERY TIME
            System.out.println("=== DEBUG: Starting job creation (FORCE MODE) ===");
            System.out.println("Current job count: " + jobRepository.count());
            
            // Delete all existing jobs first
            jobRepository.deleteAll();
            System.out.println("Deleted all existing jobs");
            
            User recruiter = userRepository.findByUsername("recruiter").orElse(null);
            Department itDept = departmentRepository.findByName("Information Technology").orElse(null);
            Department hrDept = departmentRepository.findByName("Human Resources").orElse(null);
            Department financeDept = departmentRepository.findByName("Finance").orElse(null);
            
            System.out.println("DEBUG: Recruiter found: " + (recruiter != null));
            System.out.println("DEBUG: IT Dept found: " + (itDept != null));
            System.out.println("DEBUG: HR Dept found: " + (hrDept != null));
            System.out.println("DEBUG: Finance Dept found: " + (financeDept != null));
            
            if (recruiter != null && itDept != null) {
                // Senior Java Developer
                Job javaJob = new Job();
                javaJob.setTitle("Senior Java Developer");
                javaJob.setDescription("We are looking for an experienced Java Developer to join our team.");
                javaJob.setRequirements("5+ years of Java experience, Spring Boot, Microservices");
                javaJob.setSkills("Java, Spring Boot, Microservices, REST API, SQL");
                javaJob.setMinExperience(5);
                javaJob.setMaxExperience(10);
                javaJob.setMinSalary(new BigDecimal("80000"));
                javaJob.setMaxSalary(new BigDecimal("120000"));
                javaJob.setJobType(Job.JobType.FULL_TIME);
                javaJob.setWorkLocation("Chennai");
                javaJob.setVacancies(3);
                javaJob.setCompanyName("Tech Solutions Inc.");
                javaJob.setEmploymentType("Full-time");
                javaJob.setWorkMode("Hybrid");
                javaJob.setBenefits("Health insurance, PF, ESOPs, Flexible hours");
                javaJob.setDepartment(itDept);
                javaJob.setPostedBy(recruiter);
                javaJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(javaJob);
                
                // Python Data Scientist
                Job pythonJob = new Job();
                pythonJob.setTitle("Python Data Scientist");
                pythonJob.setDescription("Looking for a Python expert in data science and machine learning.");
                pythonJob.setRequirements("3+ years of Python experience, ML frameworks, Data analysis");
                pythonJob.setSkills("Python, Machine Learning, Data Analysis, Pandas, NumPy");
                pythonJob.setMinExperience(3);
                pythonJob.setMaxExperience(7);
                pythonJob.setMinSalary(new BigDecimal("70000"));
                pythonJob.setMaxSalary(new BigDecimal("100000"));
                pythonJob.setJobType(Job.JobType.FULL_TIME);
                pythonJob.setWorkLocation("Bangalore");
                pythonJob.setVacancies(2);
                pythonJob.setCompanyName("Data Analytics Pro");
                pythonJob.setEmploymentType("Full-time");
                pythonJob.setWorkMode("Office");
                pythonJob.setBenefits("Health insurance, Gym membership, Learning budget");
                pythonJob.setDepartment(itDept);
                pythonJob.setPostedBy(recruiter);
                pythonJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(pythonJob);
                
                // Frontend Developer
                Job frontendJob = new Job();
                frontendJob.setTitle("Frontend Developer");
                frontendJob.setDescription("Creative Frontend Developer for modern web applications.");
                frontendJob.setRequirements("3+ years React/Vue experience, modern CSS, responsive design");
                frontendJob.setSkills("React, JavaScript, TypeScript, CSS, HTML, Redux");
                frontendJob.setMinExperience(3);
                frontendJob.setMaxExperience(6);
                frontendJob.setMinSalary(new BigDecimal("60000"));
                frontendJob.setMaxSalary(new BigDecimal("90000"));
                frontendJob.setJobType(Job.JobType.FULL_TIME);
                frontendJob.setWorkLocation("Chennai");
                frontendJob.setVacancies(4);
                frontendJob.setCompanyName("Web Innovations Ltd");
                frontendJob.setEmploymentType("Full-time");
                frontendJob.setWorkMode("Remote");
                frontendJob.setBenefits("Flexible hours, Remote work allowance, Tech budget");
                frontendJob.setDepartment(itDept);
                frontendJob.setPostedBy(recruiter);
                frontendJob.setDeadline(LocalDateTime.now().plusMonths(2));
                jobRepository.save(frontendJob);
                
                // DevOps Engineer
                Job devopsJob = new Job();
                devopsJob.setTitle("DevOps Engineer");
                devopsJob.setDescription("DevOps Engineer to manage cloud infrastructure and CI/CD pipelines.");
                devopsJob.setRequirements("4+ years DevOps experience, AWS, Docker, Kubernetes");
                devopsJob.setSkills("AWS, Docker, Kubernetes, Jenkins, Linux, CI/CD");
                devopsJob.setMinExperience(4);
                devopsJob.setMaxExperience(8);
                devopsJob.setMinSalary(new BigDecimal("75000"));
                devopsJob.setMaxSalary(new BigDecimal("110000"));
                devopsJob.setJobType(Job.JobType.FULL_TIME);
                devopsJob.setWorkLocation("Bangalore");
                devopsJob.setVacancies(2);
                devopsJob.setCompanyName("Cloud Systems Inc");
                devopsJob.setEmploymentType("Full-time");
                devopsJob.setWorkMode("Hybrid");
                devopsJob.setBenefits("AWS certification, Conference attendance, Home office setup");
                devopsJob.setDepartment(itDept);
                devopsJob.setPostedBy(recruiter);
                devopsJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(devopsJob);
                
                // Mobile App Developer
                Job mobileJob = new Job();
                mobileJob.setTitle("Mobile App Developer");
                mobileJob.setDescription("Mobile Developer for iOS and Android applications.");
                mobileJob.setRequirements("3+ years mobile development experience, React Native or Flutter");
                mobileJob.setSkills("React Native, Flutter, iOS, Android, JavaScript, Dart");
                mobileJob.setMinExperience(3);
                mobileJob.setMaxExperience(7);
                mobileJob.setMinSalary(new BigDecimal("65000"));
                mobileJob.setMaxSalary(new BigDecimal("95000"));
                mobileJob.setJobType(Job.JobType.FULL_TIME);
                mobileJob.setWorkLocation("Hyderabad");
                mobileJob.setVacancies(3);
                mobileJob.setCompanyName("Mobile First Solutions");
                mobileJob.setEmploymentType("Full-time");
                mobileJob.setWorkMode("Office");
                mobileJob.setBenefits("Device allowance, App store credits, Flexible schedule");
                mobileJob.setDepartment(itDept);
                mobileJob.setPostedBy(recruiter);
                mobileJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(mobileJob);
                
                // QA Engineer
                Job qaJob = new Job();
                qaJob.setTitle("QA Engineer");
                qaJob.setDescription("Quality Assurance Engineer for manual and automated testing.");
                qaJob.setRequirements("2+ years QA experience, automation tools, testing methodologies");
                qaJob.setSkills("Selenium, JUnit, TestNG, Manual Testing, Automation");
                qaJob.setMinExperience(2);
                qaJob.setMaxExperience(5);
                qaJob.setMinSalary(new BigDecimal("45000"));
                qaJob.setMaxSalary(new BigDecimal("70000"));
                qaJob.setJobType(Job.JobType.FULL_TIME);
                qaJob.setWorkLocation("Chennai");
                qaJob.setVacancies(5);
                qaJob.setCompanyName("Quality Systems Pro");
                qaJob.setEmploymentType("Full-time");
                qaJob.setWorkMode("Hybrid");
                qaJob.setBenefits("Testing tools, Certification support, Health insurance");
                qaJob.setDepartment(itDept);
                qaJob.setPostedBy(recruiter);
                qaJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(qaJob);
            }
            
            if (recruiter != null && hrDept != null) {
                // HR Manager
                Job hrJob = new Job();
                hrJob.setTitle("HR Manager");
                hrJob.setDescription("Experienced HR Manager needed for our growing team.");
                hrJob.setRequirements("5+ years HR experience, recruitment skills, team management");
                hrJob.setSkills("HR Management, Recruitment, Communication, Team Building");
                hrJob.setMinExperience(5);
                hrJob.setMaxExperience(15);
                hrJob.setMinSalary(new BigDecimal("60000"));
                hrJob.setMaxSalary(new BigDecimal("90000"));
                hrJob.setJobType(Job.JobType.FULL_TIME);
                hrJob.setWorkLocation("Mumbai");
                hrJob.setVacancies(1);
                hrJob.setCompanyName("HR Solutions Ltd");
                hrJob.setEmploymentType("Full-time");
                hrJob.setWorkMode("Office");
                hrJob.setBenefits("Leadership training, Company car, Performance bonus");
                hrJob.setDepartment(hrDept);
                hrJob.setPostedBy(recruiter);
                hrJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(hrJob);
                
                // Technical Recruiter
                Job recruiterJob = new Job();
                recruiterJob.setTitle("Technical Recruiter");
                recruiterJob.setDescription("Technical Recruiter to hire IT professionals.");
                recruiterJob.setRequirements("3+ years technical recruitment experience, IT knowledge");
                recruiterJob.setSkills("Technical Recruitment, Sourcing, Interviewing, IT Knowledge");
                recruiterJob.setMinExperience(3);
                recruiterJob.setMaxExperience(8);
                recruiterJob.setMinSalary(new BigDecimal("50000"));
                recruiterJob.setMaxSalary(new BigDecimal("80000"));
                recruiterJob.setJobType(Job.JobType.FULL_TIME);
                recruiterJob.setWorkLocation("Bangalore");
                recruiterJob.setVacancies(2);
                recruiterJob.setCompanyName("Tech Recruiters Inc");
                recruiterJob.setEmploymentType("Full-time");
                recruiterJob.setWorkMode("Hybrid");
                recruiterJob.setBenefits("Recruitment tools, Commission, Flexible hours");
                recruiterJob.setDepartment(hrDept);
                recruiterJob.setPostedBy(recruiter);
                recruiterJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(recruiterJob);
                
                // Senior Medical Officer
                Job medicalJob = new Job();
                medicalJob.setTitle("Senior Medical Officer");
                medicalJob.setDescription("Experienced Medical Officer for our healthcare facility.");
                medicalJob.setRequirements("MBBS degree, 5+ years medical practice experience, valid medical license");
                medicalJob.setSkills("Medicine, Patient Care, Diagnosis, Treatment, Medical Procedures");
                medicalJob.setMinExperience(5);
                medicalJob.setMaxExperience(15);
                medicalJob.setMinSalary(new BigDecimal("90000"));
                medicalJob.setMaxSalary(new BigDecimal("130000"));
                medicalJob.setJobType(Job.JobType.FULL_TIME);
                medicalJob.setWorkLocation("Mumbai");
                medicalJob.setVacancies(2);
                medicalJob.setCompanyName("City Hospital");
                medicalJob.setEmploymentType("Full-time");
                medicalJob.setWorkMode("Office");
                medicalJob.setBenefits("Health insurance, CME allowance, Housing allowance");
                medicalJob.setDepartment(hrDept);
                medicalJob.setPostedBy(recruiter);
                medicalJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(medicalJob);
                
                // Registered Nurse
                Job nurseJob = new Job();
                nurseJob.setTitle("Registered Nurse");
                nurseJob.setDescription("Experienced Registered Nurse for patient care.");
                nurseJob.setRequirements("Nursing degree, 2+ years experience, valid nursing license");
                nurseJob.setSkills("Patient Care, Nursing, Medical Assistance, Emergency Care, CPR");
                nurseJob.setMinExperience(2);
                nurseJob.setMaxExperience(8);
                nurseJob.setMinSalary(new BigDecimal("40000"));
                nurseJob.setMaxSalary(new BigDecimal("60000"));
                nurseJob.setJobType(Job.JobType.FULL_TIME);
                nurseJob.setWorkLocation("Delhi");
                nurseJob.setVacancies(5);
                nurseJob.setCompanyName("General Hospital");
                nurseJob.setEmploymentType("Full-time");
                nurseJob.setWorkMode("Office");
                nurseJob.setBenefits("Health insurance, Overtime pay, Training programs");
                nurseJob.setDepartment(hrDept);
                nurseJob.setPostedBy(recruiter);
                nurseJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(nurseJob);
                
                // Senior Professor
                Job professorJob = new Job();
                professorJob.setTitle("Senior Professor");
                professorJob.setDescription("Senior Professor for Computer Science department.");
                professorJob.setRequirements("PhD in Computer Science, 10+ years teaching experience, research publications");
                professorJob.setSkills("Teaching, Research, Computer Science, Programming, Academic Writing");
                professorJob.setMinExperience(10);
                professorJob.setMaxExperience(20);
                professorJob.setMinSalary(new BigDecimal("70000"));
                professorJob.setMaxSalary(new BigDecimal("100000"));
                professorJob.setJobType(Job.JobType.FULL_TIME);
                professorJob.setWorkLocation("Bangalore");
                professorJob.setVacancies(1);
                professorJob.setCompanyName("University College");
                professorJob.setEmploymentType("Full-time");
                professorJob.setWorkMode("Office");
                professorJob.setBenefits("Research funding, Conference attendance, Sabbatical leave");
                professorJob.setDepartment(hrDept);
                professorJob.setPostedBy(recruiter);
                professorJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(professorJob);
                
                // High School Teacher
                Job teacherJob = new Job();
                teacherJob.setTitle("High School Mathematics Teacher");
                teacherJob.setDescription("Mathematics teacher for grades 9-12.");
                teacherJob.setRequirements("B.Ed in Mathematics, 3+ years teaching experience, strong math background");
                teacherJob.setSkills("Mathematics, Teaching, Curriculum Development, Student Assessment, Classroom Management");
                teacherJob.setMinExperience(3);
                teacherJob.setMaxExperience(10);
                teacherJob.setMinSalary(new BigDecimal("35000"));
                teacherJob.setMaxSalary(new BigDecimal("50000"));
                teacherJob.setJobType(Job.JobType.FULL_TIME);
                teacherJob.setWorkLocation("Chennai");
                teacherJob.setVacancies(3);
                teacherJob.setCompanyName("Education Academy");
                teacherJob.setEmploymentType("Full-time");
                teacherJob.setWorkMode("Office");
                teacherJob.setBenefits("Professional development, Tuition waiver, Health insurance");
                teacherJob.setDepartment(hrDept);
                teacherJob.setPostedBy(recruiter);
                teacherJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(teacherJob);
            }
            
            if (recruiter != null && financeDept != null) {
                // Senior Accountant
                Job accountantJob = new Job();
                accountantJob.setTitle("Senior Accountant");
                accountantJob.setDescription("Senior Accountant for financial reporting and analysis.");
                accountantJob.setRequirements("5+ years accounting experience, financial reporting, tax knowledge");
                accountantJob.setSkills("Accounting, Financial Reporting, Tax, Excel, Tally");
                accountantJob.setMinExperience(5);
                accountantJob.setMaxExperience(12);
                accountantJob.setMinSalary(new BigDecimal("55000"));
                accountantJob.setMaxSalary(new BigDecimal("85000"));
                accountantJob.setJobType(Job.JobType.FULL_TIME);
                accountantJob.setWorkLocation("Mumbai");
                accountantJob.setVacancies(2);
                accountantJob.setCompanyName("Finance Experts Ltd");
                accountantJob.setEmploymentType("Full-time");
                accountantJob.setWorkMode("Office");
                accountantJob.setBenefits("CA support, Financial training, Health insurance");
                accountantJob.setDepartment(financeDept);
                accountantJob.setPostedBy(recruiter);
                accountantJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(accountantJob);
                
                // Financial Analyst
                Job analystJob = new Job();
                analystJob.setTitle("Financial Analyst");
                analystJob.setDescription("Financial Analyst for investment and budget analysis.");
                analystJob.setRequirements("3+ years financial analysis experience, modeling, reporting");
                analystJob.setSkills("Financial Analysis, Excel, Modeling, Reporting, Investment Analysis");
                analystJob.setMinExperience(3);
                analystJob.setMaxExperience(7);
                analystJob.setMinSalary(new BigDecimal("60000"));
                analystJob.setMaxSalary(new BigDecimal("90000"));
                analystJob.setJobType(Job.JobType.FULL_TIME);
                analystJob.setWorkLocation("Delhi");
                analystJob.setVacancies(3);
                analystJob.setCompanyName("Investment Analytics Pro");
                analystJob.setEmploymentType("Full-time");
                analystJob.setWorkMode("Hybrid");
                analystJob.setBenefits("CFA support, Bloomberg terminal, Performance bonus");
                analystJob.setDepartment(financeDept);
                analystJob.setPostedBy(recruiter);
                analystJob.setDeadline(LocalDateTime.now().plusMonths(1));
                jobRepository.save(analystJob);
            }
            
            System.out.println("Sample jobs created!");
            System.out.println("Total jobs created: " + jobRepository.count());
        };
    }
}
