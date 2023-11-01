package com.shopsmart.ecommerceapi.exception;

public class ResourceDoesNotExist extends RuntimeException {
    public ResourceDoesNotExist(String message) {
        super(message);
    }
}
