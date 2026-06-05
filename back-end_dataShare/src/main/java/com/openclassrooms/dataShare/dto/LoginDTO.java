package com.openclassrooms.dataShare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank
    @Email
    private String email;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    @NotBlank
    @Size(min = 8)
    private String password;
}
