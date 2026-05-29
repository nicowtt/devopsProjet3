package com.openclassrooms.dataShare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare.dto.LoginDTO;
import com.openclassrooms.dataShare.dto.RegisterDTO;
import com.openclassrooms.dataShare.entities.User;
import com.openclassrooms.dataShare.repository.UserRepository;
import com.openclassrooms.dataShare.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerTest extends AbstractIntegrationTest {

    private static final String URL = "/api/register";
    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void registerUserWithoutRequiredData() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void registerAlreadyExistUser() throws Exception {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        userService.register(user);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword(PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void registerUserSuccessful() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword(PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(objectMapper.writeValueAsString(registerDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void loginWithoutRequiredData() throws Exception {
        LoginDTO loginDTO = new LoginDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(objectMapper.writeValueAsString(loginDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // login test successful is on AbstractIntegrationTest.
    // It will be using before each authenticated integrated test.
}