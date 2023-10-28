package com.shopsmart.ecommerceapi.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiException {

    private String message;
    private HttpStatus status;


}
