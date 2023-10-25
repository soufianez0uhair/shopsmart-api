package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeAll
    public void setUp() throws Exception {
        userService = new UserService(userRepository);
    }
}
