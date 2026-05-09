package com.vasilebreban.trainticketing;

import com.vasilebreban.trainticketing.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("4.1 Create train should return 201")
    void shouldCreateTrain() throws Exception {
        String trainNumber = uniqueTrainNumber("IR-303");

        String requestBody = """
                {
                  "trainNumber": "%s",
                  "capacity": 120
                }
                """.formatted(trainNumber);

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.trainNumber").value(trainNumber))
                .andExpect(jsonPath("$.capacity").value(120))
                .andExpect(jsonPath("$.delayMinutes").value(0));
    }

    @Test
    @DisplayName("4.2 Create train with duplicate train number should return 409")
    void shouldReturnConflictForDuplicateTrainNumber() throws Exception {
        String trainNumber = uniqueTrainNumber("IR-DUPLICATE");

        String requestBody = """
                {
                  "trainNumber": "%s",
                  "capacity": 120
                }
                """.formatted(trainNumber);

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Train already exists")));
    }

    @Test
    @DisplayName("4.3 Create train with empty train number should return 400")
    void shouldReturnBadRequestForEmptyTrainNumber() throws Exception {
        String requestBody = """
                {
                  "trainNumber": "",
                  "capacity": 120
                }
                """;

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.trainNumber").exists());
    }

    @Test
    @DisplayName("4.4 Create train with invalid capacity should return 400")
    void shouldReturnBadRequestForInvalidCapacity() throws Exception {
        String requestBody = """
                {
                  "trainNumber": "IR-INVALID",
                  "capacity": 0
                }
                """;

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.capacity").exists());
    }

    @Test
    @DisplayName("4.5 Update train should return 200")
    void shouldUpdateTrain() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-UPDATE"), 100);
        String updatedTrainNumber = uniqueTrainNumber("IR-UPDATED");

        String requestBody = """
                {
                  "trainNumber": "%s",
                  "capacity": 140
                }
                """.formatted(updatedTrainNumber);

        mockMvc.perform(put("/admin/trains/" + trainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(trainId))
                .andExpect(jsonPath("$.trainNumber").value(updatedTrainNumber))
                .andExpect(jsonPath("$.capacity").value(140));
    }

    @Test
    @DisplayName("4.6 Update train with invalid id should return 404")
    void shouldReturnNotFoundWhenUpdatingInvalidTrainId() throws Exception {
        String requestBody = """
                {
                  "trainNumber": "IR-999",
                  "capacity": 140
                }
                """;

        mockMvc.perform(put("/admin/trains/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }

    @Test
    @DisplayName("4.7 Delete train should return 200")
    void shouldDeleteTrain() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-DELETE"), 100);

        mockMvc.perform(delete("/admin/trains/" + trainId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Train deleted successfully")));
    }

    @Test
    @DisplayName("4.8 Delete train with invalid id should return 404")
    void shouldReturnNotFoundWhenDeletingInvalidTrainId() throws Exception {
        mockMvc.perform(delete("/admin/trains/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }

    @Test
    @DisplayName("5.1 Get all routes should return 200")
    void shouldGetAllRoutes() throws Exception {
        mockMvc.perform(get("/admin/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("5.2 Get route by id should return 200")
    void shouldGetRouteById() throws Exception {
        mockMvc.perform(get("/admin/routes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.train").exists())
                .andExpect(jsonPath("$.stops").exists());
    }

    @Test
    @DisplayName("5.3 Get route with invalid id should return 404")
    void shouldReturnNotFoundForInvalidRouteId() throws Exception {
        mockMvc.perform(get("/admin/routes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Route not found")));
    }

    @Test
    @DisplayName("5.4 Create train before creating route should return 201")
    void shouldCreateTrainBeforeCreatingRoute() throws Exception {
        String trainNumber = uniqueTrainNumber("IR-404");

        String requestBody = """
                {
                  "trainNumber": "%s",
                  "capacity": 90
                }
                """.formatted(trainNumber);

        mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.trainNumber").value(trainNumber));
    }

    @Test
    @DisplayName("5.5 Create route for new train should return 201")
    void shouldCreateRouteForNewTrain() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-ROUTE"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "06:45"
                    },
                    {
                      "stationName": "Sibiu",
                      "stopOrder": 2,
                      "arrivalTime": "09:10",
                      "departureTime": "09:25"
                    },
                    {
                      "stationName": "Bucuresti",
                      "stopOrder": 3,
                      "arrivalTime": "14:30",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.train.id").value(trainId))
                .andExpect(jsonPath("$.stops", hasSize(3)));
    }

    @Test
    @DisplayName("5.6 Create route for train that already has route should return 409")
    void shouldReturnConflictWhenRouteAlreadyExistsForTrain() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "07:00"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Route already exists")));
    }

    @Test
    @DisplayName("5.7 Create route with duplicate stop order should return 400")
    void shouldReturnBadRequestForDuplicateStopOrder() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-DUP-ORDER"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Sibiu",
                      "stopOrder": 1,
                      "arrivalTime": "10:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Duplicate stop order")));
    }

    @Test
    @DisplayName("5.8 Create route with duplicate station should return 400")
    void shouldReturnBadRequestForDuplicateStation() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-DUP-STATION"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 2,
                      "arrivalTime": "10:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Duplicate station")));
    }

    @Test
    @DisplayName("5.9 Create route with only one stop should return 400")
    void shouldReturnBadRequestForOnlyOneStop() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-ONE-STOP"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("at least two stops")));
    }

    @Test
    @DisplayName("5.10 Create route with unknown station should return 404")
    void shouldReturnNotFoundForUnknownStation() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-UNKNOWN-STATION"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Timisoara",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Station not found")));
    }

    @Test
    @DisplayName("5.11 Create route where first stop has no departure time should return 400")
    void shouldReturnBadRequestWhenFirstStopHasNoDepartureTime() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-FIRST-NO-DEP"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": null
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("First stop must have a departure time."));
    }

    @Test
    @DisplayName("5.12 Create route where last stop has no arrival time should return 400")
    void shouldReturnBadRequestWhenLastStopHasNoArrivalTime() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-LAST-NO-ARR"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": null,
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Last stop must have an arrival time."));
    }

    @Test
    @DisplayName("5.13 Create route where intermediate stop misses arrival/departure should return 400")
    void shouldReturnBadRequestWhenIntermediateStopMissesTimes() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-MISSING-INTERMEDIATE"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Sibiu",
                      "stopOrder": 2,
                      "arrivalTime": "10:00",
                      "departureTime": null
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 3,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Intermediate stop must have both arrival and departure time")));
    }

    @Test
    @DisplayName("5.14 Create route where departure is before arrival should return 400")
    void shouldReturnBadRequestWhenDepartureIsBeforeArrival() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-BAD-TIME"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Sibiu",
                      "stopOrder": 2,
                      "arrivalTime": "10:00",
                      "departureTime": "09:50"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 3,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Departure time cannot be before arrival time")));
    }

    @Test
    @DisplayName("5.15 Update route should return 200")
    void shouldUpdateRoute() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-UPDATE-ROUTE"), 90);
        Long routeId = createSimpleRouteAndReturnId(trainId);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "07:00"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": "12:20"
                    },
                    {
                      "stationName": "Bucuresti",
                      "stopOrder": 3,
                      "arrivalTime": "15:30",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(put("/admin/routes/" + routeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(routeId))
                .andExpect(jsonPath("$.stops", hasSize(3)));
    }

    @Test
    @DisplayName("5.16 Update route with invalid route id should return 404")
    void shouldReturnNotFoundWhenUpdatingInvalidRouteId() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-INVALID-ROUTE-UPDATE"), 90);

        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "07:00"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        mockMvc.perform(put("/admin/routes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Route not found")));
    }

    @Test
    @DisplayName("5.17 Delete route should return 200")
    void shouldDeleteRoute() throws Exception {
        Long trainId = createTrainAndReturnId(uniqueTrainNumber("IR-DELETE-ROUTE"), 90);
        Long routeId = createSimpleRouteAndReturnId(trainId);

        mockMvc.perform(delete("/admin/routes/" + routeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Route deleted successfully")));
    }

    @Test
    @DisplayName("5.18 Delete route with invalid id should return 404")
    void shouldReturnNotFoundWhenDeletingInvalidRouteId() throws Exception {
        mockMvc.perform(delete("/admin/routes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Route not found")));
    }

    @Test
    @DisplayName("6.1 Get bookings for train should return 200")
    void shouldGetBookingsForTrain() throws Exception {
        String bookingBody = """
                {
                  "trainId": 1,
                  "customerName": "Admin Booking User",
                  "customerEmail": "admin.booking@example.com",
                  "numberOfTickets": 1
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/admin/trains/1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("6.2 Get bookings for invalid train should return 404")
    void shouldReturnNotFoundWhenGettingBookingsForInvalidTrain() throws Exception {
        mockMvc.perform(get("/admin/trains/999/bookings"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }

    @Test
    @DisplayName("6.3 Mark train as delayed should return 200")
    void shouldMarkTrainAsDelayed() throws Exception {
        String requestBody = """
                {
                  "delayMinutes": 35
                }
                """;

        mockMvc.perform(post("/admin/trains/1/delay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.delayMinutes").value(35));
    }

    @Test
    @DisplayName("6.4 Mark invalid train as delayed should return 404")
    void shouldReturnNotFoundWhenMarkingInvalidTrainAsDelayed() throws Exception {
        String requestBody = """
                {
                  "delayMinutes": 35
                }
                """;

        mockMvc.perform(post("/admin/trains/999/delay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }

    @Test
    @DisplayName("6.5 Mark train as delayed with zero delay should return 400")
    void shouldReturnBadRequestForZeroDelay() throws Exception {
        String requestBody = """
                {
                  "delayMinutes": 0
                }
                """;

        mockMvc.perform(post("/admin/trains/1/delay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.delayMinutes").exists());
    }

    @Test
    @DisplayName("6.6 Mark train as delayed with negative delay should return 400")
    void shouldReturnBadRequestForNegativeDelay() throws Exception {
        String requestBody = """
                {
                  "delayMinutes": -10
                }
                """;

        mockMvc.perform(post("/admin/trains/1/delay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.delayMinutes").exists());
    }

    private Long createTrainAndReturnId(String trainNumber, int capacity) throws Exception {
        String requestBody = """
                {
                  "trainNumber": "%s",
                  "capacity": %d
                }
                """.formatted(trainNumber, capacity);

        String response = mockMvc.perform(post("/admin/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return TestJsonUtils.extractLong(response, "id");
    }

    private Long createSimpleRouteAndReturnId(Long trainId) throws Exception {
        String requestBody = """
                {
                  "trainId": %d,
                  "stops": [
                    {
                      "stationName": "Cluj-Napoca",
                      "stopOrder": 1,
                      "arrivalTime": null,
                      "departureTime": "08:00"
                    },
                    {
                      "stationName": "Brasov",
                      "stopOrder": 2,
                      "arrivalTime": "12:00",
                      "departureTime": null
                    }
                  ]
                }
                """.formatted(trainId);

        String response = mockMvc.perform(post("/admin/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return TestJsonUtils.extractLong(response, "id");
    }

    private String uniqueTrainNumber(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}