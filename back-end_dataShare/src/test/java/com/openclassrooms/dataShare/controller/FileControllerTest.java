package com.openclassrooms.dataShare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.dataShare.dto.FileDTO;
import com.openclassrooms.dataShare.repository.FileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FileControllerTest extends AbstractIntegrationTest {

    private static final String URL = "/api/files";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
    void test_upload_file_returns_201() throws Exception {
        // GIVEN
        FileDTO fileDTO = new FileDTO();
        fileDTO.setDayBeforeExpiration(7L);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello world".getBytes());
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json",
            objectMapper.writeValueAsBytes(fileDTO));

        // WHEN / THEN
        mockMvc.perform(multipart(URL).file(file).file(metadata))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    void test_upload_without_metadata_returns_400() throws Exception {
        // GIVEN
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello world".getBytes());

        // WHEN / THEN
        mockMvc.perform(multipart(URL).file(file))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}
