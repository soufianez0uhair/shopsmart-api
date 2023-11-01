package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.dto.AuthResponse;
import com.shopsmart.ecommerceapi.dto.LoginRequest;
import com.shopsmart.ecommerceapi.exception.ResourceAlreadyExists;
import com.shopsmart.ecommerceapi.exception.ResourceDoesNotExist;
import com.shopsmart.ecommerceapi.model.Role;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.RoleRepository;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private JWTUtils jwtUtils;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            JWTUtils jwtUtils,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }

    public AuthResponse registerCustomer(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isPresent()) {
            throw new ResourceAlreadyExists("Email is already in use");
        }
        Optional<Role> role = roleRepository.findByName("customer");
        user.addRole(role.get());
        userRepository.save(user);
        return AuthResponse.builder().token(jwtUtils.generateToken(user)).build();
    }

    public AuthResponse loginCustomer(LoginRequest request) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()) {
            throw new ResourceDoesNotExist("Email is not linked to any account", "email");
        }
        User user = optionalUser.get();
        if(!user.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("Incorrect password");
        }
        String token = jwtUtils.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }
}
