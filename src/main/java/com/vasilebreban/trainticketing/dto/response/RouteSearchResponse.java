package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteSearchResponse {
    private String type;
    private List<RouteSegmentResponse> segments;
}