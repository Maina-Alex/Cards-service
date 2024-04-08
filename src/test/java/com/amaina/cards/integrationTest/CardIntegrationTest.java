package com.amaina.cards.integrationTest;

import com.amaina.cards.constant.CardStatus;
import com.amaina.cards.dto.CreateCardDto;
import com.amaina.cards.dto.UpdateCardDto;
import com.amaina.cards.model.AppUser;
import com.amaina.cards.model.Card;
import com.amaina.cards.repository.AppUserRepository;
import com.amaina.cards.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
public class CardIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CardRepository cardRepository;
    @Autowired
    AppUserRepository appUserRepository;


    ObjectMapper objectMapper = new ObjectMapper();


    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>(DockerImageName.parse("mysql:latest"));


    @BeforeEach
    void loadTestData(){
        AppUser appUser = appUserRepository.findById(1L).orElse(null);
        List<Card> cardList= List.of(
                Card.builder()
                        .appUser(appUser)
                        .name("Test card")
                        .color("#FFOOOO")
                        .description("Test card1")
                        .status(CardStatus.TO_DO)
                        .build(),
                Card.builder()
                        .appUser(appUser)
                        .name("Test card2")
                        .color("#FFFOOO")
                        .description("Test card2")
                        .status(CardStatus.TO_DO)
                        .build()
        );
        cardRepository.saveAll(cardList);
    }


    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void createCardSuccess() throws Exception {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .name("Test Card")
                .description("This is a testing card")
                .color("#FFFFFF")
                .build();
        mockMvc.perform(post("/api/v1/cards/create")
                        .content(objectMapper.writeValueAsString(createCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(CardStatus.TO_DO.name()));
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void createCardWithNoNameFails() throws Exception {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .description("This is a testing card")
                .color("#FFFFFF")
                .build();
        mockMvc.perform(post("/api/v1/cards/create")
                        .content(objectMapper.writeValueAsString(createCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void createCardWithInvalidColor() throws Exception {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .description("This is a testing card")
                .color("FFFF")
                .build();
        mockMvc.perform(post("/api/v1/cards/create")
                        .content(objectMapper.writeValueAsString(createCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void searchCardSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/search")
                        .queryParam("name", "Test Card")
                        .queryParam("color", "#FFOOOO")
                        .queryParam("status", "TO_DO")
                        .queryParam("cardSort", "STATUS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cards").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRecords").exists());
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void updateCardSuccess() throws Exception {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .cardId(1L)
                .cardStatus(CardStatus.IN_PROGRESS)
                .description("Update card")
                .color(null)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cards/update")
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Update card"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(CardStatus.IN_PROGRESS.name()));
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void updateCardNotFoundByIdFailed() throws Exception {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .cardId(0L)
                .cardStatus(CardStatus.IN_PROGRESS)
                .description("Update card")
                .color(null)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cards/update")
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void deleteCardSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/delete/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "john@example.com", authorities = "ADMIN")
    void deleteCardNotFoundByIdFailed() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/delete/{id}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
