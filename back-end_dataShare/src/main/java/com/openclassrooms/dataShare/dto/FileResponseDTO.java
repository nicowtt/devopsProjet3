package com.openclassrooms.dataShare.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileResponseDTO {

    private UUID uuid;

    private String name;

    private Long size;

    private LocalDateTime expiredAt;

    private boolean hasPassword;
}
