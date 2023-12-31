package com.shopsmart.ecommerceapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ecommerceapi.dto.AuthResponse;
import com.shopsmart.ecommerceapi.dto.LoginRequest;
import com.shopsmart.ecommerceapi.exception.ApiException;
import com.shopsmart.ecommerceapi.model.Role;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.RoleRepository;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                AuthResponse.class
        );

        // Then

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(user.getEmail(), jwtUtils.extractEmail(response.getBody().getToken()));

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

    @Test
    public void givenUserWithInvalidEmail_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test.com")
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
        assertEquals("Please use a valid email", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenUserWithOver256LengthEmail_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("FubH2FDt4QcAvYjpDvURgz1Ej7J06nrRUWAuSaP9DZAvEC8K5gZy5nqLDFzTbCw2VD5J4xfWSiDJW9VSUvXixZganH0h6TkGM2Pyt2SQBiB6Mcydp69Cm3hM8qp9XASipBMjRU15GNu2LDa6Kac5A3Jxp4BwFLqb8veS8DfbaaqdTatrZS07STzAZJjqWBhwXgK2qcKQ4554wTd0g1Jr2DithEyHEVw1UG6r0JM9SGbenJHVMwMhTir@gmail.com")
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
        assertEquals("Please use a valid email", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenPasswordWithOver20LengthPassword_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("testtesttesttesttestt")
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
        assertEquals("Password must not exceed 20 characters length", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    public void givenPasswordWithLessThan8CharactersLengthPassword_whenRegisterCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@12")
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
        assertEquals("Password must be at least 8 characters long", response.getBody().getMessage());
        assertEquals("400 BAD_REQUEST", response.getBody().getHttpStatus().toString());
    }

    @Test
    @Sql(statements = "INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES ('test', 'test', 'test@test.com', '+212600000000', 'test@12')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from users", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenUserWithAlreadyInUseEmail_whenRegisterCustomer_thenReturn409AndMessage() throws JsonProcessingException {
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
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/register",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        assertNotNull(optionalUser);
        assertEquals("Email is already in use", response.getBody().getMessage());
        assertEquals("409 CONFLICT", response.getBody().getHttpStatus().toString());
    }

    @Test
    @Sql(statements = "INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES ('test', 'test', 'test@test.com', '+212600000000', 'test@123')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from users", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenExistingUserEmailAndPassword_whenLoginCustomer_thenReturn200AndToken() throws JsonProcessingException {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/v1/users/login",
                httpEntity,
                AuthResponse.class
        );

        // Then
        Optional<User> optionalSavedUser = userRepository.findByEmail(loginRequest.getEmail());
        assertTrue(optionalSavedUser.isPresent());

        assertEquals(loginRequest.getEmail(), jwtUtils.extractEmail(response.getBody().getToken()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void givenNonExistingUserEmail_whenLoginCustomer_thenReturn401AndMessage() throws JsonProcessingException {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/login",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is not linked to any account", response.getBody().getMessage());
        assertEquals("email", response.getBody().getField());
    }

    @Test
    public void givenInvalidUserEmail_whenLoginCustomer_thenReturn401AndMessage() throws JsonProcessingException {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test")
                .password("test@123")
                .build();

        String requestBody = mapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/login",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email", response.getBody().getMessage());
    }

    @Test
    @Sql(statements = "INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES ('test', 'test', 'test@test.com', '+212600000000', 'test@12')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from users", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenUserWithWrongPassword_whenLoginCustomer_thenReturn400AndMessage() throws JsonProcessingException {

        // Given

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("wrongPass")
                .build();

        String requestBody = mapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // when

        ResponseEntity<ApiException> response = restTemplate.postForEntity(
                "/api/v1/users/login",
                httpEntity,
                ApiException.class
        );

        // Then

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody().getMessage());
    }

}
