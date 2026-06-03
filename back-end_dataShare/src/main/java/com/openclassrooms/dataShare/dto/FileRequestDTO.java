package com.openclassrooms.dataShare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileRequestDTO {

    @NotNull
    @Min(1)
    @Max(7)
    private Long dayBeforeExpiration;

    @Size(min = 6)
    private String password;
}