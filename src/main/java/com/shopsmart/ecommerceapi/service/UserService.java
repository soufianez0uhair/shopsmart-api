package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private JWTUtils jwtUtils;

    @Autowired
    public UserService(
            UserRepository userRepository,
            JWTUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public String registerCustomer(User user) {
        userRepository.findByEmail(user.getEmail());
        userRepository.save(user);
        return jwtUtils.generateToken(user);
    }
}
