package com.shopsmart.ecommerceapi.exception;

import lombok.Getter;

@Getter
public class ResourceDoesNotExist extends RuntimeException {

    private String message;
    private String field;

    public ResourceDoesNotExist(String message, String field) {
        this.message = message;
        this.field = field;
    }
}
