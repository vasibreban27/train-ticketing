package com.vasilebreban.trainticketing;

import com.vasilebreban.trainticketing.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("3.1 Create booking should return 201")
    void shouldCreateBooking() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Vasile Breban",
                  "customerEmail": "vasilebreban2017@gmail.com",
                  "numberOfTickets": 2
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numberOfTickets").value(2))
                .andExpect(jsonPath("$.customer.fullName").value("Vasile Breban"))
                .andExpect(jsonPath("$.customer.email").value("vasilebreban2017@gmail.com"));
    }

    @Test
    @DisplayName("3.2 Create another booking on same train should return 201")
    void shouldCreateAnotherBookingOnSameTrain() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Alex Pop",
                  "customerEmail": "alex.pop@example.com",
                  "numberOfTickets": 3
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numberOfTickets").value(3))
                .andExpect(jsonPath("$.customer.fullName").value("Alex Pop"))
                .andExpect(jsonPath("$.customer.email").value("alex.pop@example.com"));
    }

    @Test
    @DisplayName("3.3 Get booking by id should return 200")
    void shouldGetBookingById() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Booking Lookup User",
                  "customerEmail": "booking.lookup@example.com",
                  "numberOfTickets": 1
                }
                """;

        String response = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = TestJsonUtils.extractLong(response, "id");

        mockMvc.perform(get("/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.customer.email").value("booking.lookup@example.com"));
    }

    @Test
    @DisplayName("3.4 Get booking with invalid id should return 404")
    void shouldReturnNotFoundForInvalidBookingId() throws Exception {
        mockMvc.perform(get("/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Booking not found")));
    }

    @Test
    @DisplayName("3.5 Overbooking should return 409")
    void shouldReturnConflictForOverbooking() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Overbooking Test",
                  "customerEmail": "overbooking@example.com",
                  "numberOfTickets": 200
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Not enough seats available")));
    }

    @Test
    @DisplayName("3.6 Booking with invalid email should return 400")
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Invalid Email User",
                  "customerEmail": "not-an-email",
                  "numberOfTickets": 2
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.customerEmail").exists());
    }

    @Test
    @DisplayName("3.7 Booking with zero tickets should return 400")
    void shouldReturnBadRequestForZeroTickets() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Zero Tickets User",
                  "customerEmail": "zero@example.com",
                  "numberOfTickets": 0
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.numberOfTickets").exists());
    }

    @Test
    @DisplayName("3.8 Booking with negative tickets should return 400")
    void shouldReturnBadRequestForNegativeTickets() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "Negative Tickets User",
                  "customerEmail": "negative@example.com",
                  "numberOfTickets": -5
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.numberOfTickets").exists());
    }

    @Test
    @DisplayName("3.9 Booking with missing trainId should return 400")
    void shouldReturnBadRequestForMissingTrainId() throws Exception {
        String requestBody = """
                {
                  "customerName": "Missing Train User",
                  "customerEmail": "missing.train@example.com",
                  "numberOfTickets": 2
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.trainId").exists());
    }

    @Test
    @DisplayName("3.10 Booking with missing customer name should return 400")
    void shouldReturnBadRequestForMissingCustomerName() throws Exception {
        String requestBody = """
                {
                  "trainId": 1,
                  "customerName": "",
                  "customerEmail": "missing.name@example.com",
                  "numberOfTickets": 2
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.customerName").exists());
    }

    @Test
    @DisplayName("3.11 Booking with invalid train id should return 404")
    void shouldReturnNotFoundForInvalidTrainIdInBooking() throws Exception {
        String requestBody = """
                {
                  "trainId": 999,
                  "customerName": "Invalid Train User",
                  "customerEmail": "invalid.train@example.com",
                  "numberOfTickets": 2
                }
                """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Train not found")));
    }
}