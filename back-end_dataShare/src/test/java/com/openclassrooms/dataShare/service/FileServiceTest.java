package com.openclassrooms.dataShare.service;

import com.openclassrooms.dataShare.dto.FileDTO;
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
        FileDTO expectedDTO = new FileDTO();
        expectedDTO.setDayBeforeExpiration(7L);

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(fileRepository.save(any())).thenReturn(file);
        when(fileDTOMapper.toDTO(file)).thenReturn(expectedDTO);

        // WHEN
        FileDTO result = fileService.upload(multipartFile, file, 7L, owner);

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
        when(fileDTOMapper.toDTO(any())).thenReturn(new FileDTO());

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
}