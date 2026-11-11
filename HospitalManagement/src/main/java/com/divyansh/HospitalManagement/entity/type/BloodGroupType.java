package com.divyansh.HospitalManagement.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;

// Enum defining standardized blood group values used across DTOs and Database entity
// Next Flow: Used in Patient entity (@Enumerated) and PatientRequestDto/PatientResponseDto for type safety
public enum BloodGroupType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String symbol;

    BloodGroupType(String symbol) {
        this.symbol = symbol;
    }

    // Returns standard medical shorthand symbol (e.g. "A+", "O-")
    public String getSymbol() {
        return symbol;
    }

    // JSON Deserializer & Parser: Accepts standard enum names ("A_POSITIVE"), symbols ("A+"), and lowercase ("a_positive", "a+")
    // Next Flow: Jackson calls this method when parsing incoming JSON payloads in Request DTOs
    @JsonCreator
    public static BloodGroupType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String cleanValue = value.trim().toUpperCase().replace(" ", "_");
        return switch (cleanValue) {
            case "A+", "A_POSITIVE", "A_POS" -> A_POSITIVE;
            case "A-", "A_NEGATIVE", "A_NEG" -> A_NEGATIVE;
            case "B+", "B_POSITIVE", "B_POS" -> B_POSITIVE;
            case "B-", "B_NEGATIVE", "B_NEG" -> B_NEGATIVE;
            case "AB+", "AB_POSITIVE", "AB_POS" -> AB_POSITIVE;
            case "AB-", "AB_NEGATIVE", "AB_NEG" -> AB_NEGATIVE;
            case "O+", "O_POSITIVE", "O_POS" -> O_POSITIVE;
            case "O-", "O_NEGATIVE", "O_NEG" -> O_NEGATIVE;
            default -> throw new IllegalArgumentException(
                    "Invalid blood group: '" + value + "'. Accepted values are: A+, A-, B+, B-, AB+, AB-, O+, O- (or A_POSITIVE, B_POSITIVE, etc.)"
            );
        };
    }
}