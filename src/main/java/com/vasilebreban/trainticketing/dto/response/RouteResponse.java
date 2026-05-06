package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteResponse {
    private Long id;
    private TrainResponse train;
    private List<RouteStopResponse> stops;
}