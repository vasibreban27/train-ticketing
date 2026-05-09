package com.vasilebreban.trainticketing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("1.1 Get all trains should return 200 and list of trains")
    void shouldGetAllTrains() throws Exception {
        mockMvc.perform(get("/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("1.2 Get train by id should return 200")
    void shouldGetTrainById() throws Exception {
        mockMvc.perform(get("/trains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trainNumber").exists())
                .andExpect(jsonPath("$.capacity").exists())
                .andExpect(jsonPath("$.delayMinutes").exists());
    }

    @Test
    @DisplayName("1.3 Get train with invalid id should return 404")
    void shouldReturnNotFoundForInvalidTrainId() throws Exception {
        mockMvc.perform(get("/trains/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }
}