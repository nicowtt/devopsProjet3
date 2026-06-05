package com.openclassrooms.dataShare.security;

import com.openclassrooms.dataShare.controller.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SecurityConfigTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    // AUTHENTICATED ROUTE TEST
    @Test
    void test_get_files_route_returns_401_when_not_authorized() throws Exception {
        mockMvc.perform(get("/api/files"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    void test_delete_file_route_returns_401_when_not_authorized() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        mockMvc.perform(delete("/api/files/" + uuid))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}