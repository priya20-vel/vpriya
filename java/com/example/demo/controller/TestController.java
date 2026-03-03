package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Application is working! Time: " + java.time.LocalDateTime.now();
    }
    
    @GetMapping("/status")
    public String status() {
        return "AI Resume Screening System is running! Access the frontend at: <a href='/index.html'>Click here</a>";
    }
}
