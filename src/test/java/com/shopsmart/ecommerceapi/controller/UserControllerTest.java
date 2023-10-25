package com.shopsmart.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

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

        given(userService.registerCustomer(requestBody)).willReturn("ExpectedReturnValue");

        // When

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody))
                .accept(MediaType.APPLICATION_JSON)
        )
        // Then
                .andExpect(status().isCreated())
                .andExpect(content().string("ExpectedReturnValue"));
    }

}
