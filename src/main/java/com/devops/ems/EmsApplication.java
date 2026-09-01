package com.devops.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Employee Management System.
 *
 * Plain Spring Boot application — no Docker, Jenkins, or CI/CD wiring.
 * Run it with: mvn spring-boot:run
 * Or build a jar with: mvn clean package  ->  java -jar target/employee-management-system.jar
 */
@SpringBootApplication
public class EmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmsApplication.class, args);
    }
}
