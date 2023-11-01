package com.shopsmart.ecommerceapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiException apiException = ApiException
                .builder()
                .message(errors.get(0))
                .timestamp(new Date())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<ApiException> handleResourceAlreadyExistsException(ResourceAlreadyExists e) {

        ApiException apiException = ApiException
                .builder()
                .message(e.getMessage())
                .timestamp(new Date())
                .httpStatus(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(
                apiException,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ResourceDoesNotExist.class)
    public ResponseEntity<ApiException> handleResourceDoesNotExistException(ResourceDoesNotExist e) {

        ApiException apiException = ApiException
                .builder()
                .message(e.getMessage())
                .timestamp(new Date())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .field("email")
                .build();

        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
