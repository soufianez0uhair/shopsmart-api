package com.shopsmart.ecommerceapi.controller;

import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<String> registerCustomer(@RequestBody User user) {
        return new ResponseEntity<String>(
                userService.registerCustomer(user),
                HttpStatus.CREATED
        );
    }
}
