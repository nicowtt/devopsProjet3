package com.openclassrooms.dataShare.controller;

import com.openclassrooms.dataShare.dto.FileDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Fichiers", description = "Upload et gestion des fichiers")
@RestController
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
    @PostMapping(path = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> upload(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("metadata") FileDTO fileDTO,
            @AuthenticationPrincipal User user
    ) {

        FileDTO saved = fileService.upload(
            file,
            fileDTOMapper.toEntity(fileDTO),
            fileDTO.getDayBeforeExpiration(),
            user
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}