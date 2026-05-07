package com.vasilebreban.trainticketing.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> validationErrors;
    private String path;
}