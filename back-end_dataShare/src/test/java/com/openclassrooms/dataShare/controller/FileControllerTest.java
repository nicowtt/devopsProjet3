package com.openclassrooms.dataShare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare.dto.FileRequestDTO;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.repository.FileRepository;
import com.openclassrooms.dataShare.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FileControllerTest extends AbstractIntegrationTest {

    private static final String URL = "/api/files";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoSpyBean
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void afterEach() {
        fileRepository.deleteAll();
    }

    @Test
    void test_upload_returns_201() throws Exception {
        // GIVEN
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello world".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        // THEN
        mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    void test_get_file_returns_404_when_not_found() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // THEN
        mockMvc.perform(get(URL + "/" + uuid))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void test_upload_returns_503_when_storage_fails() throws Exception {
        // GIVEN
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello world".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        doThrow(new FileStorageException("Échec disk", new RuntimeException()))
            .when(fileService).upload(any(), any(), any(), any());

        // THEN
        mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andDo(print())
            .andExpect(status().isServiceUnavailable());
    }
}