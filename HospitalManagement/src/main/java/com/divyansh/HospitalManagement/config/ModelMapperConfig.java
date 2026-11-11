package com.divyansh.HospitalManagement.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuration Layer: Declares third-party utility beans in the Spring ApplicationContext
// Next Flow: Provides the ModelMapper bean for dependency injection into PatientService
@Configuration
public class ModelMapperConfig {

    // Bean Definition: Instantiates and exposes ModelMapper as a Spring Bean
    // Next Flow: Automatically injected into PatientService to effortlessly map DTOs <-> Entities
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
