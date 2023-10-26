package com.shopsmart.ecommerceapi.service;

import com.shopsmart.ecommerceapi.model.User;
import com.shopsmart.ecommerceapi.repository.UserRepository;
import com.shopsmart.ecommerceapi.util.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private UserService underTest;

    @Test
    public void givenNewCustomer_whenRegisterCustomer_thenReturnTokenWithCustomerRole() {
        // Given
        User user = User
                .builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(jwtUtils.generateToken(user)).willReturn("someReturnedToken");

        // When
        String returnedValue = underTest.registerCustomer(user);
        // Then
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByEmail(stringCaptor.capture());
        assertEquals(user.getEmail(), stringCaptor.getValue());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User passedUserToSave = userCaptor.getValue();
        assertEquals("test", passedUserToSave.getFirstName());
        assertEquals("test", passedUserToSave.getLastName());
        assertEquals("test@test.com", passedUserToSave.getEmail());
        assertEquals("+212600000000", passedUserToSave.getPhoneNumber());
        assertEquals("test@123", passedUserToSave.getPassword());
        assertEquals("someReturnedToken", returnedValue);
        verify(jwtUtils).generateToken(userCaptor.capture());
        User passedUserToJWT = userCaptor.getValue();
        assertEquals("test", passedUserToJWT.getFirstName());
        assertEquals("test", passedUserToJWT.getLastName());
        assertEquals("test@test.com", passedUserToJWT.getEmail());
        assertEquals("+212600000000", passedUserToJWT.getPhoneNumber());
        assertEquals("test@123", passedUserToJWT.getPassword());
    }
}
