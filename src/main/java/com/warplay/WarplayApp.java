package com.warplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarplayApp {
    public static void main(String[] args) {
        // Set JVM timezone to UTC for consistent timestamp handling
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        
        SpringApplication.run(WarplayApp.class, args);
        System.out.println("Warplay Campaign Manager is running!");
        System.out.println("Application started successfully on port 8080");
    }
}
