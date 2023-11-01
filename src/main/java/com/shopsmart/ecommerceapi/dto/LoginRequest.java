package com.shopsmart.ecommerceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class LoginRequest {

    private String email;
    private String password;
}
