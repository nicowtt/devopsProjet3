package com.openclassrooms.dataShare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare.dto.FileRequestDTO;
import com.openclassrooms.dataShare.dto.LoginDTO;
import com.openclassrooms.dataShare.dto.RegisterDTO;
import com.openclassrooms.dataShare.exception.FileSizeExceededException;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.repository.FileRepository;
import com.openclassrooms.dataShare.repository.UserRepository;
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

import java.io.InputStream;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FileControllerTest extends AbstractIntegrationTest {

    private static final String URL = "/api/files";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // to intercept and throw exception (example: we can't upload more than 1 giga file)
    @MockitoSpyBean
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @AfterEach
    void afterEach() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String registerAndLogin(String email, String password) throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(email);
        registerDTO.setPassword(password);
        mockMvc.perform(post("/api/users")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDTO)));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);
        return mockMvc.perform(post("/api/users/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andReturn().getResponse().getContentAsString();
    }

    // UPLOAD FILE
    @Test
    void test_upload_with_no_user_authenticated_returns_201() throws Exception {
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
    void test_upload_returns_413_when_file_exceeds_1GB() throws Exception {
        // GIVEN
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);

        MockMultipartFile file = new MockMultipartFile("file", "big.pdf", "application/pdf", "content".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        doThrow(new FileSizeExceededException("Fichier trop volumineux"))
            .when(fileService).uploadFile(any(), any(), any(), any());

        // THEN
        mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andDo(print())
            .andExpect(status().isContentTooLarge());
    }

    @Test
    void test_upload_returns_415_when_exe_hidden_as_txt() throws Exception {
        // GIVEN - executable Windows file .exe hidden on .txt
        byte[] exeContent;
        try (InputStream is = getClass().getResourceAsStream("/testFiles/malicious.exe")) {
            exeContent = is.readAllBytes();
        }
        MockMultipartFile file = new MockMultipartFile("file", "malicious.txt", "text/plain", exeContent);

        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        // THEN
        mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andDo(print())
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void test_upload_returns_503_when_storage_fails() throws Exception {
        // GIVEN
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        doThrow(new FileStorageException("Échec disk", new RuntimeException()))
            .when(fileService).uploadFile(any(), any(), any(), any());

        // THEN
        mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andDo(print())
            .andExpect(status().isServiceUnavailable());
    }

    // GET FILES
    @Test
    void test_get_file_returns_404_when_not_found() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // THEN
        mockMvc.perform(get(URL + "/" + uuid))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    // DOWNLOAD FILE
    @Test
    void test_download_file_returns_403_when_wrong_password() throws Exception {
        // GIVEN
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);
        fileRequestDTO.setPassword("password");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        String uploadResponse = mockMvc.perform(multipart(URL).file(file).part(metadata))
            .andReturn().getResponse().getContentAsString();
        String uuid = objectMapper.readTree(uploadResponse).get("uuid").asText();

        // THEN
        mockMvc.perform(get(URL + "/download/" + uuid)
                .param("password", "wrongPassword"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    // DELETE FILES
    @Test
    void test_delete_file_returns_204() throws Exception {
        // GIVEN - register + login
        String token = this.registerAndLogin("test@gmail.com", "password");

        // Upload with user
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        String uploadResponse = mockMvc.perform(multipart(URL).file(file).part(metadata)
                .header("Authorization", "Bearer " + token))
            .andReturn().getResponse().getContentAsString();
        String uuid = objectMapper.readTree(uploadResponse).get("uuid").asText();

        // THEN
        mockMvc.perform(delete(URL + "/" + uuid)
                .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    void test_delete_file_returns_403_when_not_owner() throws Exception {
        // GIVEN - user "1@gmail.com" upload file
        String tokenA = this.registerAndLogin("1@gmail.com", "password");
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setDayBeforeExpiration(7L);
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        MockPart metadata = new MockPart("metadata", objectMapper.writeValueAsBytes(fileRequestDTO));
        metadata.getHeaders().setContentType(APPLICATION_JSON);

        String uploadResponse = mockMvc.perform(multipart(URL).file(file).part(metadata)
                .header("Authorization", "Bearer " + tokenA))
            .andReturn().getResponse().getContentAsString();
        String uuid = objectMapper.readTree(uploadResponse).get("uuid").asText();

        // GIVEN - user "2@gmail.com" try to remove "1@gmail.com" user file
        String tokenB = this.registerAndLogin("2@gmail.com", "password");

        // THEN
        mockMvc.perform(delete(URL + "/" + uuid)
                .header("Authorization", "Bearer " + tokenB))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void test_delete_file_returns_404_when_not_found() throws Exception {
        // GIVEN - register + login
        String token = this.registerAndLogin("test@gmail.com", "password");
        UUID uuid = UUID.randomUUID();

        // THEN
        mockMvc.perform(delete(URL + "/" + uuid)
                .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}