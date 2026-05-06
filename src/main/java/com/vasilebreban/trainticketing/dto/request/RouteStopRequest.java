package com.vasilebreban.trainticketing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RouteStopRequest {

    @NotBlank
    private String stationName;

    @NotNull
    @Min(1)
    private Integer stopOrder;

    private LocalTime arrivalTime;
    private LocalTime departureTime;
}
