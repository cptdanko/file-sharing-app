package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {

    }

    @Test
    void testAuthUser_success() {
        Optional<FileUser> optionalUser = Optional.of(FileUser.builder().name("Cpt.Danko").build());
        ServiceResponse userResp = ServiceResponse.builder().data(optionalUser).build();
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userService.getByUsername(any())).thenReturn(userResp);
        String token = UUID.randomUUID().toString();
        when(jwtService.generateToken(any())).thenReturn(token);
        ServiceResponse response = authService
                .authenticateUser(AuthRequest.builder().username("cpt.danko").password("***").build());
        assertEquals(response.getStatus().intValue(), HttpStatus.OK.value());
        assertFalse(response.isError());
        LoginResponse r = (LoginResponse) response.getData();
        assertEquals(r.getAccessToken(), token);
    }

    @Test
    void testAuthUser_fail() {
        Optional<FileUser> optionalUser = Optional.of(FileUser.builder().name("Cpt.Dano").build());
        ServiceResponse userResp = ServiceResponse.builder().data(optionalUser).build();
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            authService.authenticateUser(AuthRequest.builder().username("cpt.danko").password("***").build());
        });
    }

    @Test
    void testLoginViaGoogle_success() {
        GoogleUserInfoRequest googleUserInfoRequest = GoogleUserInfoRequest.builder()
                .email("b@mydaytodo.com").name("cptdanko")
                .pictureLink("https://mydaytodo.com/blog").build();
        when(userService.saveUser(any())).thenReturn(TestUtils.successResponse);
        String token = UUID.randomUUID().toString();
        when(jwtService.generateToken(any())).thenReturn(token);
        ServiceResponse response = authService.loginViaGoogle(googleUserInfoRequest);

        verify(userService, times(1)).saveUser(any());
        verify(jwtService, times(1)).generateToken(any());

        assertEquals(response.getStatus().intValue(), HttpStatus.OK.value());
        assertEquals(response.getData().getClass(), TokenResponse.class);
        TokenResponse r = (TokenResponse) response.getData();
        assertEquals(r.getAccessToken(), token);
    }

    @Test
    void testLoginViaGoogle_fail() {
        GoogleUserInfoRequest googleUserInfoRequest = GoogleUserInfoRequest.builder()
                .email("b@mydaytodo.com").name("cptdanko")
                .pictureLink("https://mydaytodo.com/blog").build();
        when(userService.saveUser(any())).thenReturn(TestUtils.internalServerResponse);

        ServiceResponse response = authService.loginViaGoogle(googleUserInfoRequest);
        verify(jwtService, times(0)).generateToken(any());
        assertEquals(response.getStatus().intValue(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
