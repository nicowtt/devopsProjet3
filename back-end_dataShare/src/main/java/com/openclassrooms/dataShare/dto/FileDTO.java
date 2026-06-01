package com.openclassrooms.dataShare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileDTO {

    private UUID uuid;

    private String name;

    @NotNull
    @Min(1)
    private Long dayBeforeExpiration;

    private LocalDateTime expiredAt;

    @Size(min = 6)
    private String password;
}