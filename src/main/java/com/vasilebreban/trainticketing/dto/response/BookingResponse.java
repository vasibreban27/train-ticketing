package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingResponse {
    private Long id;
    private TrainResponse train;
    private CustomerResponse customer;
    private Integer numberOfTickets;
    private LocalDateTime bookingTime;
}