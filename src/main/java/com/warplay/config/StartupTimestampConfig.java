package com.warplay.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class StartupTimestampConfig {
    
    @PostConstruct
    public void setStartupTimestamp() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        System.setProperty("startup.timestamp", timestamp);
    }
}
