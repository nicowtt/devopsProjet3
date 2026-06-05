package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.exception.FileSizeExceededException;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.exception.InvalidFileTypeException;
import com.openclassrooms.dataShare.exception.ResourceNotFoundException;
import com.openclassrooms.dataShare.mapper.FileDTOMapper;
import com.openclassrooms.dataShare.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private static final long MAX_FILE_SIZE = 1024L * 1024 * 1024; // 1 GB

    // executable file cant be uploaded for server security
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/svg+xml", "image/webp",
        "video/mp4", "video/x-matroska",
        "audio/mpeg", "audio/wav", "audio/ogg", "audio/flac", "audio/aac", "audio/mp4",
        "application/pdf", "text/plain",
        "application/zip", "application/x-tar"
    );

    private final FileRepository fileRepository;
    private final FileDTOMapper fileDTOMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileResponseDTO uploadFile(
        MultipartFile multipartFile,
        File file,
        Long dayBeforeExpiration,
        User owner
    ) {
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("Fichier trop volumineux, maximum 1 Go");
        }
        String detectedType;
        try {
            detectedType = new Tika().detect(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new FileStorageException("Impossible de lire le fichier", e);
        }
        if (!ALLOWED_MIME_TYPES.contains(detectedType)) {
            throw new InvalidFileTypeException("Type de fichier non autorisé : " + detectedType);
        }
        file.setName(multipartFile.getOriginalFilename());
        file.setSize(multipartFile.getSize());
        file.setMimeType(multipartFile.getContentType());
        file.setExpiredAt(this.computeExpiredAt(dayBeforeExpiration));
        file.setOwner(owner);

        File saved = fileRepository.save(file);
        log.info("File saved: uuid={}", saved.getUuid());

        this.saveToLocalStorage(multipartFile, file.getUuid());

        return fileDTOMapper.toFileResponseDTO(saved);
    }

    public List<FileResponseDTO> getFiles(User user) {
        return fileRepository.findAllByOwner(user).stream()
            .map(fileDTOMapper::toFileResponseDTO)
            .toList();
    }

    public FileResponseDTO getFile(UUID fileUuid) {
        // TODO check password match and encode/decode with Bcrypt
        File fileDb = fileRepository.findByUuid(fileUuid)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return fileDTOMapper.toFileResponseDTO(fileDb);

    }

    public void deleteFile(UUID fileUuid, User user) {
        File fileDb = fileRepository.findByUuid(fileUuid)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (!fileDb.getOwner().equals(user)) {
            throw new AccessDeniedException("User can only delete his own files");
        }

        fileRepository.delete(fileDb);
        log.info("File removed: uuid={}", fileDb.getUuid());
    }

    // -------------------------- private methods -----------------------------

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