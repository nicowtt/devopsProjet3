package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileDTO;
import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.mapper.FileDTOMapper;
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
    private final FileDTOMapper fileDTOMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileDTO upload(
        MultipartFile multipartFile,
        File file,
        Long dayBeforeExpiration,
        User owner
    ) {

        file.setName(multipartFile.getOriginalFilename());
        file.setSize(multipartFile.getSize());
        file.setMimeType(multipartFile.getContentType());
        file.setExpiredAt(this.computeExpiredAt(dayBeforeExpiration));
        file.setOwner(owner);

        File saved = fileRepository.save(file);
        log.info("File saved: uuid={}", saved.getUuid());


        this.saveToLocalStorage(multipartFile, file.getUuid());

        return fileDTOMapper.toDTO(saved);
    }

    private LocalDateTime computeExpiredAt(Long dayBeforeExpiration) {
        return LocalDateTime.now().plusDays(dayBeforeExpiration);
    }

    private void saveToLocalStorage(MultipartFile multipartFile, UUID fileUuid) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            multipartFile.transferTo(uploadPath.resolve(fileUuid.toString()));
        } catch (IOException e) {
            throw new FileStorageException("Échec de la sauvegarde du fichier uuid=" + fileUuid, e);
        }
    }
}