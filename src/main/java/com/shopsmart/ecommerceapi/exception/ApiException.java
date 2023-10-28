package com.shopsmart.ecommerceapi.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
public class ApiException {

    private String message;
    private HttpStatus httpStatus;
    private Date timestamp;

}
