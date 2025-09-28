package com.warplay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "application", "WarPlay Campaign Manager",
                "version", "1.0.0"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
                "message", "Welcome to WarPlay Campaign Manager!",
                "description", "Ready to manage your tabletop campaigns"
        );
    }
}