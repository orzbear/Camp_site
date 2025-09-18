package com.outscout.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * OutScout Backend Application
 * Smart Picnic & Camping Planner
 */
@SpringBootApplication
@EnableAsync
public class OutScoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutScoutApplication.class, args);
    }
}
