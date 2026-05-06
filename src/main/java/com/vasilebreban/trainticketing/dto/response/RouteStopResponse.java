package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class RouteStopResponse {
    private Long id;
    private String stationName;
    private Integer stopOrder;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
}