package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileDTO;
import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileDTO upload(MultipartFile multipartFile, FileDTO fileDTO, User owner) throws IOException {
        UUID fileUuid = UUID.randomUUID();

        saveToLocalStorage(multipartFile, fileUuid);

        File file = new File();
        file.setUuid(fileUuid);
        file.setName(multipartFile.getOriginalFilename());
        file.setSize(multipartFile.getSize());
        file.setMimeType(multipartFile.getContentType());
        file.setExpiredAt(computeExpiredAt(fileDTO.getDayBeforeExpiration()));
        file.setPassword(fileDTO.getPassword());
        file.setOwner(owner);

        File saved = fileRepository.save(file);
        log.info("File saved: uuid={}, owner={}", saved.getUuid(), owner.getUsername());

        return toDTO(saved, fileDTO.getDayBeforeExpiration());
    }

    private LocalDateTime computeExpiredAt(Long dayBeforeExpiration) {
        return LocalDateTime.now().plusDays(dayBeforeExpiration);
    }

    private void saveToLocalStorage(MultipartFile multipartFile, UUID fileUuid) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        Path destination = uploadPath.resolve(fileUuid.toString());
        multipartFile.transferTo(destination);
    }

    private FileDTO toDTO(File file, Long dayBeforeExpiration) {
        FileDTO dto = new FileDTO();
        dto.setUuid(file.getUuid());
        dto.setName(file.getName());
        dto.setSize(file.getSize());
        dto.setMimeType(file.getMimeType());
        dto.setCreatedAt(file.getCreatedAt());
        dto.setDayBeforeExpiration(dayBeforeExpiration);
        return dto;
    }
}