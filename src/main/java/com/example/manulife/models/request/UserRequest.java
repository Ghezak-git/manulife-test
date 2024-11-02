package com.example.manulife.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    
    @NotBlank(message = "Full Name is required")
    @JsonProperty("full_name")
    private String fullName;
    
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String password;

    @NotBlank(message = "Status is required")
    private String status;

}
