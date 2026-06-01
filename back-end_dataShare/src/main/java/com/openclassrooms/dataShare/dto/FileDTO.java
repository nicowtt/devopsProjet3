package com.openclassrooms.dataShare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileDTO {

    private UUID uuid;

    private String name;

    private Long size;

    private String mimeType;

    private LocalDateTime createdAt;

    @NotNull
    @Min(1)
    private Long dayBeforeExpiration;

    private String password;
}