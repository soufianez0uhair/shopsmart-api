package com.shopsmart.ecommerceapi.controller;

import com.shopsmart.ecommerceapi.dto.AuthResponse;
import com.shopsmart.ecommerceapi.dto.LoginRequest;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.model.ValidationSequenceOrder;
import com.shopsmart.ecommerceapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<AuthResponse> registerCustomer(@Validated(ValidationSequenceOrder.class) @RequestBody User user) {
        return new ResponseEntity<>(
                userService.registerCustomer(user),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    private ResponseEntity<AuthResponse> loginCustomer(@Valid @RequestBody LoginRequest request) throws AuthenticationException {
        return ResponseEntity.ok(userService.loginCustomer(request));
    }
}
