package com.amaina.cards.integrationTest;

import com.amaina.cards.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
public class LoginIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @Test
    void loginAdminSuccess() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("root123")
                .build();
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").exists());
    }

    @Test
    void loginMemberSuccess() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("jane@example.com")
                .password("root123")
                .build();
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").exists());
    }

    @Test
    void invalidLogin() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("anonymous@example.com")
                .password("noPassword")
                .build();
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }


}
