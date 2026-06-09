package com.openclassrooms.dataShare.controller;

import com.openclassrooms.dataShare.dto.FileRequestDTO;
import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.mapper.FileDTOMapper;
import com.openclassrooms.dataShare.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Fichiers", description = "Upload et gestion des fichiers")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileDTOMapper fileDTOMapper;


    // ---------------------------- WRITE ---------------
    @Operation(
        summary = "Upload d'un fichier",
        responses = {
            @ApiResponse(responseCode = "201", description = "Fichier téléversé avec succès"),
            @ApiResponse(responseCode = "413", description = "Fichier trop volumineux, maximum 1 Go"),
            @ApiResponse(responseCode = "415", description = "Type de fichier non supporté"),
            @ApiResponse(responseCode = "503", description = "Le service est temporairement indisponible"),
        }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> upload(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("metadata") FileRequestDTO fileRequestDTO,
            @AuthenticationPrincipal User user
    ) {
        FileResponseDTO saved = fileService.uploadFile(
            file,
            fileDTOMapper.toEntity(fileRequestDTO),
            fileRequestDTO.getDayBeforeExpiration(),
            user
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // ---------------------------- READ ---------------
    @Operation(
        summary = "Consultation des fichiers d'un utilisateur",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
        }
    )
    @GetMapping
    public List<FileResponseDTO> getFiles(@AuthenticationPrincipal User user) {
        return fileService.getFiles(user);
    }

    @Operation(
        summary = "Affichage des métadonnées d'un fichier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
        }
    )
    @GetMapping("/{uuid}")
    public FileResponseDTO getFile(@PathVariable UUID uuid) {
        return fileService.getFile(uuid);
    }

    @Operation(
        summary = "Authorization du téléchargement d'un fichier (mot de passe optionnel)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "403", description = "Mot de passe nécessaire ou mauvais mot de passe"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
        }
    )
    @PostMapping("/download/{uuid}")
    public FileResponseDTO downloadFile(
        @PathVariable UUID uuid,
        @RequestBody(required = false) String password
    ) {
        return fileService.downloadFile(uuid, password);
    }

    // ---------------------------- DELETE ---------------
    @Operation(
        summary = "Suppression d'un fichier",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "403", description = "Un utilisateur peut uniquement supprimer ses fichiers"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
        }
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteFile(
        @PathVariable UUID uuid,
        @AuthenticationPrincipal User user
    ) {
        fileService.deleteFile(uuid, user);
        return ResponseEntity.noContent().build();
    }
}