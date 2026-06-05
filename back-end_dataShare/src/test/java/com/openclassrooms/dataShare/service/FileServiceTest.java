package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.exception.FileSizeExceededException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;

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
    private PasswordEncoder passwordEncoder;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
    }

    // UPLOAD FILE
    @Test
    void test_upload_File_compute_expired_at() throws IOException {
        // GIVEN
        User owner = new User();
        File file = new File();
        file.setUuid(UUID.randomUUID());
        LocalDateTime before = LocalDateTime.now().plusDays(7);

        when(multipartFile.getInputStream()).thenReturn(getClass().getResourceAsStream("/testFiles/test.pdf"));
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(fileRepository.save(any())).thenReturn(file);
        when(fileDTOMapper.toFileResponseDTO(any())).thenReturn(new FileResponseDTO());

        // WHEN
        fileService.uploadFile(multipartFile, file, 7L, owner);

        // THEN
        assertThat(file.getExpiredAt()).isAfterOrEqualTo(before);
        assertThat(file.getExpiredAt()).isBefore(before.plusSeconds(5));
    }

    // GET FILE
    @Test
    void test_get_File_with_hasPassword_false_when_no_password() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        File file = new File();
        file.setUuid(uuid);
        FileResponseDTO dto = new FileResponseDTO();

        when(fileRepository.findByUuid(uuid)).thenReturn(Optional.of(file));
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(dto);

        // WHEN
        FileResponseDTO result = fileService.getFile(uuid);

        // THEN
        assertThat(result.isHasPassword()).isFalse();
    }

    // DOWNLOAD FILE
    @Test
    void test_download_file_succeeds_when_correct_password() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        File file = new File();
        file.setUuid(uuid);
        file.setPassword("password");
        FileResponseDTO dto = new FileResponseDTO();

        when(fileRepository.findByUuid(uuid)).thenReturn(Optional.of(file));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(dto);

        // WHEN
        FileResponseDTO result = fileService.downloadFile(uuid, "password");

        // THEN
        assertThat(result).isEqualTo(dto);
    }

    // GET FILES
    @Test
    void test_get_Files_with_user_returns_list_of_user_dto() {
        // GIVEN
        User user = new User();
        File file = new File();
        file.setOwner(user);
        file.setName("name");
        FileResponseDTO dto = new FileResponseDTO();
        dto.setName("name");

        when(fileRepository.findAllByOwner(user)).thenReturn(List.of(file));
        when(fileDTOMapper.toFileResponseDTO(file)).thenReturn(dto);

        // WHEN
        List<FileResponseDTO> result = fileService.getFiles(user);

        // THEN
        assertThat(result.getFirst().getName()).isEqualTo("name");
        assertThat(result).hasSize(1);
    }
}