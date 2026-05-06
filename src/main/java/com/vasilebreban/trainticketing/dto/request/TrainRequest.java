package com.vasilebreban.trainticketing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainRequest {

    @NotBlank
    private String trainNumber;

    @NotNull
    @Min(1)
    private Integer capacity;

}
