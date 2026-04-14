package com.kaarigar.backend.dto;

import jakarta.validation.constraints.*; // Concept: Jakarta Validation API
import lombok.Data;

@Data
public class WorkerProfileDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience seems unrealistic")
    private int experienceYears;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    private String videoUrl;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
}