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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JWTUtils jwtUtils;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService underTest;

    @Test
    public void givenNewCustomer_whenRegisterCustomer_thenReturnToken() {
        // Given
        User user = User
                .builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();
        User userSpy = spy(user);
        given(userRepository.findByEmail(userSpy.getEmail())).willReturn(Optional.empty());
        given(jwtUtils.generateToken(userSpy)).willReturn("someReturnedToken");
        given(roleRepository.findByName(anyString())).willReturn(Optional.of(new Role(1, "customer")));

        // When
        AuthResponse authResponse = underTest.registerCustomer(userSpy);
        // Then
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByEmail(stringCaptor.capture());
        assertEquals(userSpy.getEmail(), stringCaptor.getValue());

        verify(roleRepository).findByName("customer");

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(userSpy).addRole(roleCaptor.capture());
        assertEquals("customer", roleCaptor.getValue().getName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User passedUserToSave = userCaptor.getValue();
        assertEquals("test", passedUserToSave.getFirstName());
        assertEquals("test", passedUserToSave.getLastName());
        assertEquals("test@test.com", passedUserToSave.getEmail());
        assertEquals("+212600000000", passedUserToSave.getPhoneNumber());
        assertEquals("test@123", passedUserToSave.getPassword());

        verify(jwtUtils).generateToken(userCaptor.capture());
        User passedUserToJWT = userCaptor.getValue();
        assertEquals("test", passedUserToJWT.getFirstName());
        assertEquals("test", passedUserToJWT.getLastName());
        assertEquals("test@test.com", passedUserToJWT.getEmail());
        assertEquals("+212600000000", passedUserToJWT.getPhoneNumber());
        assertEquals("test@123", passedUserToJWT.getPassword());
        assertEquals("someReturnedToken", authResponse.getToken());

    }

    @Test
    public void givenUserWithAlreadyInUseEmail_whenRegistercustomer_throwResourceAlreadyExists() {

        // Given

        User user = User
                .builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212614671572")
                .password("test@123")
                .build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // When & Then
        Exception exception = assertThrows(ResourceAlreadyExists.class, () -> {
            underTest.registerCustomer(user);
        });
        assertEquals("Email is already in use", exception.getMessage());

    }

    @Test
    public void givenExistingUserEmailAndPassword_whenLoginCustomer_thenReturnToken() throws AuthenticationException {

        // Given

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("test@123")
                .build();

        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .phoneNumber("+212600000000")
                .password("test@123")
                .build();

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        given(userRepository.findByEmail(stringCaptor.capture())).willReturn(Optional.of(user));
        given(jwtUtils.generateToken(userCaptor.capture())).willReturn("someToken");
        // When
        AuthResponse authResponse = underTest.loginCustomer(loginRequest);
        // Then
        assertEquals("someToken", authResponse.getToken());

        String capturedEmail = stringCaptor.getValue();
        assertEquals("test@test.com", capturedEmail);
        assertEquals("test@test.com", userCaptor.getValue().getEmail());

    }

    @Test
    public void givenNonExistingUserEmail_whenLoginCustomer_thenThrowResourceDoesNotExistAndMessage() {

        // Given

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("test@123")
                .build();

        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());
        // When & Then
        ResourceDoesNotExist exception = assertThrows(ResourceDoesNotExist.class, () -> {
            underTest.loginCustomer(loginRequest);
        });
        assertEquals("Email is not linked to any account", exception.getMessage());
        assertEquals("email", exception.getField());

    }


}
