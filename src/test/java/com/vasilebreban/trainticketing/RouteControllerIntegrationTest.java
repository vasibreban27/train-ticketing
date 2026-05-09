package com.vasilebreban.trainticketing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("2.1 Search direct route should return 200 and DIRECT route")
    void shouldSearchDirectRoute() throws Exception {
        mockMvc.perform(get("/routes/search")
                        .param("from", "Cluj-Napoca")
                        .param("to", "Brasov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].type").value("DIRECT"))
                .andExpect(jsonPath("$[0].segments[0].departureStation").value("Cluj-Napoca"))
                .andExpect(jsonPath("$[0].segments[0].arrivalStation").value("Brasov"));
    }

    @Test
    @DisplayName("2.2 Search route with changeover should return 200")
    void shouldSearchRouteWithChangeover() throws Exception {
        mockMvc.perform(get("/routes/search")
                        .param("from", "Cluj-Napoca")
                        .param("to", "Iasi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("2.3 Search route with no connection should return 404")
    void shouldReturnNotFoundForNoConnection() throws Exception {
        mockMvc.perform(get("/routes/search")
                        .param("from", "Iasi")
                        .param("to", "Cluj-Napoca"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No route found")));
    }

    @Test
    @DisplayName("2.4 Search route with unknown departure station should return 404")
    void shouldReturnNotFoundForUnknownDepartureStation() throws Exception {
        mockMvc.perform(get("/routes/search")
                        .param("from", "Timisoara")
                        .param("to", "Brasov"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No route found")));
    }

    @Test
    @DisplayName("2.5 Search route with unknown arrival station should return 404")
    void shouldReturnNotFoundForUnknownArrivalStation() throws Exception {
        mockMvc.perform(get("/routes/search")
                        .param("from", "Cluj-Napoca")
                        .param("to", "Constanta"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No route found")));
    }
}