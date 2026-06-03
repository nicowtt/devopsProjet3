package com.openclassrooms.dataShare.controller;

import com.openclassrooms.dataShare.dto.FileRequestDTO;
import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.mapper.FileDTOMapper;
import com.openclassrooms.dataShare.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Fichiers", description = "Upload et gestion des fichiers")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileDTOMapper fileDTOMapper;

    @Operation(
        summary = "Upload d'un fichier",
        responses = {
            @ApiResponse(responseCode = "201", description = "Fichier uploadé avec succès"),
            @ApiResponse(responseCode = "503", description = "Le service est temporairement indisponible"),
        }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> upload(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("metadata") FileRequestDTO fileRequestDTO,
            @AuthenticationPrincipal User user
    ) {
        FileResponseDTO saved = fileService.upload(
            file,
            fileDTOMapper.toEntity(fileRequestDTO),
            fileRequestDTO.getDayBeforeExpiration(),
            user
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "Téléchargement d'un fichier",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
        }
    )
    @GetMapping("/{uuid}")
    public FileResponseDTO getFile(
        @PathVariable UUID uuid
    ) {
        return fileService.get(uuid);
    }
}