package com.openclassrooms.dataShare.controller;

import com.openclassrooms.dataShare.dto.RegisterDTO;
import com.openclassrooms.dataShare.mapper.UserDTOMapper;
import com.openclassrooms.dataShare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(userDTOMapper.toEntity(registerDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
