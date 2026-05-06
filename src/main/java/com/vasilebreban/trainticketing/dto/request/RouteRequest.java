package com.vasilebreban.trainticketing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RouteRequest {
    @NotNull
    private Long trainId;

    @NotEmpty
    @Valid
    private List<RouteStopRequest> stops;
}
