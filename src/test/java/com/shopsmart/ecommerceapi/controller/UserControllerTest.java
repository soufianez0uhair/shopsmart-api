package com.shopsmart.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ecommerceapi.dto.AuthResponse;
import com.shopsmart.ecommerceapi.dto.LoginRequest;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenValidUser_whenRegisterCustomer_thenReturn201AndToken() throws Exception {

        // Given
        User requestBody = User
                .builder()
                        .firstName("test")
                                .lastName("test")
                                        .email("test@test.com")
                                                .phoneNumber("+212600000000")
                                                        .password("test@123")
                                                                .build();

        AuthResponse authResponse = AuthResponse.builder().token("someToken").build();

        given(userService.registerCustomer(requestBody)).willReturn(authResponse);

        // When

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody))
                .accept(MediaType.APPLICATION_JSON)
        )
        // Then
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(authResponse)));
    }

    @Test
    public void givenExistingUserEmailAndPassword_whenLoginCustomer_thenReturn200AndToken() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("test@123")
                .build();

        AuthResponse authResponse = AuthResponse.builder().token("someToken").build();

        given(userService.loginCustomer(loginRequest)).willReturn(authResponse);

        // When

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authResponse)));
    }

}
