package com.example.demo.repository;

import com.example.demo.entity.Department;
import com.example.demo.entity.Job;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByDepartment(Department department);
    List<Job> findByDepartmentAndIsActive(Department department, boolean isActive);
    List<Job> findByIsActive(boolean isActive);
    List<Job> findByPostedBy(User postedBy);
    List<Job> findByDepartmentName(String departmentName);
    List<Job> findByWorkLocationContaining(String location);

    // Simple search methods without complex case-insensitive features
    List<Job> findByTitleContainingAndIsActive(String title, boolean isActive);
    List<Job> findByDescriptionContainingAndIsActive(String description, boolean isActive);
    List<Job> findBySkillsContainingAndIsActive(String skills, boolean isActive);
    
    // Custom query for better search functionality
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(j.title LIKE %:keyword% OR j.description LIKE %:keyword% OR j.skills LIKE %:keyword%)")
    List<Job> searchJobsByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.skills LIKE %:skills%")
    List<Job> findRecommendedJobsBySkills(@Param("skills") String skills);

    @Query("SELECT d.name, COUNT(j) FROM Job j JOIN j.department d " +
            "WHERE j.isActive = true GROUP BY d.name")
    List<Object[]> countJobsByDepartment();

    @Query("SELECT j.postedBy.fullName, COUNT(j) FROM Job j " +
            "WHERE j.isActive = true GROUP BY j.postedBy.fullName ORDER BY COUNT(j) DESC")
    List<Object[]> findTopRecruiters();

    @Query("SELECT COUNT(ja) FROM JobApplication ja")
    long countTotalApplications();

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.status = 'PENDING'")
    long countPendingApplications();

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.status = 'SHORTLISTED'")
    long countShortlistedApplications();

    @Query("SELECT COUNT(j) FROM Job j WHERE j.isActive = true")
    long countByIsActive(boolean isActive);
}
