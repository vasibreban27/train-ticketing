package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class RouteSegmentResponse {
    private Long trainId;
    private String trainNumber;
    private String departureStation;
    private String arrivalStation;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
}