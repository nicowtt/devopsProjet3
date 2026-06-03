package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.mapper.FileDTOMapper;
import com.openclassrooms.dataShare.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private FileRepository fileRepository;
    @Mock
    private FileDTOMapper fileDTOMapper;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
    }

    @Test
    void test_upload_saves_to_db_and_returns_dto() {
        // GIVEN
        User owner = new User();
        File file = new File();
        file.setUuid(UUID.randomUUID());
        FileResponseDTO expectedDTO = new FileResponseDTO();

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(fileRepository.save(any())).thenReturn(file);
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(expectedDTO);

        // WHEN
        FileResponseDTO result = fileService.upload(multipartFile, file, 7L, owner);

        // THEN
        verify(fileRepository).save(file);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void test_upload_computes_expired_at_from_days() {
        // GIVEN
        User owner = new User();
        File file = new File();
        file.setUuid(UUID.randomUUID());
        LocalDateTime before = LocalDateTime.now().plusDays(7);

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(fileRepository.save(any())).thenReturn(file);
        when(fileDTOMapper.toFileResponseDTO(any())).thenReturn(new FileResponseDTO());

        // WHEN
        fileService.upload(multipartFile, file, 7L, owner);

        // THEN
        assertThat(file.getExpiredAt()).isAfterOrEqualTo(before);
        assertThat(file.getExpiredAt()).isBefore(before.plusSeconds(5));
    }

    @Test
    void test_upload_throws_FileStorageException_when_disk_write_fails() throws IOException {
        // GIVEN
        User owner = new User();
        File file = new File();
        file.setUuid(UUID.randomUUID());

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(fileRepository.save(any())).thenReturn(file);
        doThrow(new IOException("disk full")).when(multipartFile).transferTo(any(Path.class));

        // THEN
        assertThrows(FileStorageException.class,
            () -> fileService.upload(multipartFile, file, 7L, owner));
    }

    @Test
    void test_get_throws_ResourceNotFoundException_when_uuid_not_found() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(fileRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // THEN
        assertThrows(com.openclassrooms.dataShare.exception.ResourceNotFoundException.class,
            () -> fileService.get(uuid));
    }

    @Test
    void test_get_returns_dto_with_protected_true_when_password_set() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        File file = new File();
        file.setUuid(uuid);
        file.setPassword("secret");
        FileResponseDTO dto = new FileResponseDTO();

        when(fileRepository.findByUuid(uuid)).thenReturn(Optional.of(file));
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(dto);

        // WHEN
        FileResponseDTO result = fileService.get(uuid);

        // THEN
        assertThat(result.isHasPassword()).isTrue();
    }

    @Test
    void test_get_returns_dto_with_protected_false_when_no_password() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        File file = new File();
        file.setUuid(uuid);
        FileResponseDTO dto = new FileResponseDTO();

        when(fileRepository.findByUuid(uuid)).thenReturn(Optional.of(file));
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(dto);

        // WHEN
        FileResponseDTO result = fileService.get(uuid);

        // THEN
        assertThat(result.isHasPassword()).isFalse();
    }
}