package com.shopsmart.ecommerceapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ecommerceapi.exception.ApiException;
import com.shopsmart.ecommerceapi.model.Role;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.RoleRepository;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    @Container
    public static PostgreSQLContainer<?> psql = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureTestcontainersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", psql::getJdbcUrl);
        registry.add("spring.datasource.username", psql::getUsername);
        registry.add("spring.datasource.password", psql::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JWTUtils jwtUtils;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Sql(statements = "delete from users", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
        assertEquals(user.getEmail(), jwtUtils.extractEmail(response.getBody()));

        Optional<User> optionalSavedUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalSavedUser.isPresent());
        User savedUser = optionalSavedUser.get();
        assertEquals(1, savedUser.getRoles().size());
        Optional<Role> role = roleRepository.findByName("customer");
        assertTrue(role.isPresent());
        assertTrue(savedUser.getRoles().contains(role.get()));
    }

    @Test
    public void givenUserWithEmptyFirstName_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("")
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
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("First name is required", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithEmptyLastName_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Last name is required", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithEmptyEmail_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Email is required", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithEmptyPhoneNumber_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Phone number is required", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithEmptyPassword_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Password is required", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithNotOnlyLettersContainingFirstName_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test0")
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
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Please use a valid first name", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithNotOnlyLettersContainingLastName_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test0")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Please use a valid last name", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithOverMaxLastNameLength_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("Lorem ipsum dolor sit amet consectetur adipisw")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Please use a valid last name", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithOverMaxFirstNameLength_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("Lorem ipsum dolor sit amet consectetur adipisw")
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
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertTrue(optionalUser.isEmpty());
        assertEquals("Please use a valid first name", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

}
