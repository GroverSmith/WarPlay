package com.warplay.campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarplayApp {
    public static void main(String[] args) {
        SpringApplication.run(WarplayApp.class, args);
        System.out.println("Warplay Campaign Manager is running!");
        System.out.println("Application started successfully on port 8080");
    }
}
