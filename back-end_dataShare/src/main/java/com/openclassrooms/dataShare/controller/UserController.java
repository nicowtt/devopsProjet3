package com.openclassrooms.dataShare.controller;

import com.openclassrooms.dataShare.dto.LoginDTO;
import com.openclassrooms.dataShare.dto.RegisterDTO;
import com.openclassrooms.dataShare.mapper.UserDTOMapper;
import com.openclassrooms.dataShare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @Operation(
        summary = "Enregistrement d'un nouvel utilisateur",
        responses = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé", content = @Content)
        }
    )
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(userDTOMapper.toEntity(registerDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
        summary = "Connexion d'un utilisateur",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie, retourne le token JWT"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects", content = @Content)
        }
    )
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        String jwtToken = userService.login(userDTOMapper.toEntity(loginDTO));
        return ResponseEntity.ok(jwtToken);
    }
}