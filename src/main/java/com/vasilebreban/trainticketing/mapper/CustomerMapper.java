package com.vasilebreban.trainticketing.mapper;

import com.vasilebreban.trainticketing.dto.response.CustomerResponse;
import com.vasilebreban.trainticketing.model.Customer;

public class CustomerMapper {
    private CustomerMapper() {
    }

    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .build();
    }
}
