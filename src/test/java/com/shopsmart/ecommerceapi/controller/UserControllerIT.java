package com.shopsmart.ecommerceapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ecommerceapi.model.Role;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.RoleRepository;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenValidUser_whenCreatingCustomer_thenReturn201AndToken() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                        .lastName("test")
                                .email("test@test.com")
                                        .phoneNumber("+212600000000")
                                                .password("test@123")
                                                        .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                String.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(user.getEmail(), JWTUtils.extractUsername(response.getBody()));

        Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
        assertTrue(savedUser.isPresent());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains("customer"));
    }
}
