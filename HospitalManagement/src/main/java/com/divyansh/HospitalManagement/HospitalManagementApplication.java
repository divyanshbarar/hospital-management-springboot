package com.divyansh.HospitalManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Entry point of the Hospital Management Spring Boot Application
// Next Flow: Bootstraps the Spring ApplicationContext, initializes Auto-Configurations, and starts embedded Tomcat server
@SpringBootApplication
public class HospitalManagementApplication {

	// Main method: Launches the Spring Boot application
	// Next Flow: Scans packages, configures beans (Controller, Service, Repository), and establishes DB connection
	public static void main(String[] args) {
		SpringApplication.run(HospitalManagementApplication.class, args);
	}
}
