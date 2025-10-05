package com.warplay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class TestController {

    @GetMapping("/cors")
    public ResponseEntity<Map<String, String>> testCors() {
        return ResponseEntity.ok(Map.of(
            "message", "CORS test successful",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    @PostMapping("/cors")
    public ResponseEntity<Map<String, String>> testCorsPost(@RequestBody Map<String, String> data) {
        return ResponseEntity.ok(Map.of(
            "message", "CORS POST test successful",
            "received", data.toString(),
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "warplay-backend",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
