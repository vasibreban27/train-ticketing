package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainResponse {
    private Long id;
    private String trainNumber;
    private Integer capacity;
    private Integer delayMinutes;
}
