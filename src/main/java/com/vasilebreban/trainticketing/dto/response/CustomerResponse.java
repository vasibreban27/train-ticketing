package com.vasilebreban.trainticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String email;
}
