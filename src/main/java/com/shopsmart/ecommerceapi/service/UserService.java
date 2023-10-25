package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerCustomer(User user) {
        return "";
    }
}
