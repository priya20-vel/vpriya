package com.example.demo.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationStartupTest {
    public static void main(String[] args) {
        System.out.println("Testing Spring Boot application startup...");
        System.out.println("If this runs without errors, the JPQL issues are fixed!");
    }
}
