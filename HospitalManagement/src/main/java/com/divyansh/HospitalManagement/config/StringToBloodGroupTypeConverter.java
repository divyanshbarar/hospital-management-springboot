package com.divyansh.HospitalManagement.config;

import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Converter: Automatically converts URL query parameters (e.g. ?bloodGroup=O+ or ?bloodGroup=a_positive) to BloodGroupType enum
// Next Flow: Spring MVC calls this before invoking PatientController.getAllPatients()
@Component
public class StringToBloodGroupTypeConverter implements Converter<String, BloodGroupType> {

    // Converts incoming string query parameter to matching BloodGroupType enum
    @Override
    public BloodGroupType convert(String source) {
        return BloodGroupType.fromString(source);
    }
}
