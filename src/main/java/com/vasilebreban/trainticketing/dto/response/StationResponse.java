package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationResponse {
    private Long id;
    private String name;
}
