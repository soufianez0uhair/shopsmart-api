package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.model.Role;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.RoleRepository;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String registerCustomer(User user) {
        userRepository.findByEmail(user.getEmail());
        Optional<Role> role = roleRepository.findByName("customer");
        user.addRole(role.get());
        userRepository.save(user);
        return jwtUtils.generateToken(user);
    }
}
