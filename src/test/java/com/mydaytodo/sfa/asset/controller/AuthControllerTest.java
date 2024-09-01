package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.model.AuthRequest;
import com.mydaytodo.sfa.asset.model.GoogleUserInfoRequest;
import com.mydaytodo.sfa.asset.service.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private AuthController authController;

    JacksonTester<AuthRequest> authRequestTester;

    JacksonTester<GoogleUserInfoRequest> userInfoGoogleJacksonTester;

    private AuthRequest authRequest;
    private GoogleUserInfoRequest googleUserInfoRequest;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .build();
        authRequest = AuthRequest.builder().username("xc").password("xc").build();
        googleUserInfoRequest = GoogleUserInfoRequest.builder()
                .email("b@mydaytodo.com").pictureLink("/picture").name("Cpt.Danko")
                .build();
    }
    @Test
    void testAuthenticateAndGetToken_success() throws Exception {
        when(authService.authenticateUser(any())).thenReturn(TestUtils.successResponse);
        String url = "/api/auth/login";
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestTester.write(authRequest).getJson()))
                .andExpect(status().isOk());
    }
    @Test
    void testAuthenticateAndGetToken_fail() throws Exception {
        authRequest.setPassword("");

        String url = "/api/auth/login";
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestTester.write(authRequest).getJson()))
                .andExpect(status().isBadRequest());

        authRequest = AuthRequest.builder().username("").password("xc").build();
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestTester.write(authRequest).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGoogleLogin_success() throws Exception {
        when(authService.loginViaGoogle(any())).thenReturn(TestUtils.successResponse);
        mockMvc.perform(post("/api/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInfoGoogleJacksonTester.write(googleUserInfoRequest).getJson()))
                .andExpect(status().isOk());
    }

    @Test
    void testGoogleLogin_fail() throws Exception {
        googleUserInfoRequest.setEmail("");

        mockMvc.perform(post("/api/auth/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userInfoGoogleJacksonTester.write(googleUserInfoRequest).getJson()))
                .andExpect(status().isBadRequest());
        googleUserInfoRequest = GoogleUserInfoRequest.builder()
                .email("b@mydaytodo.com").pictureLink("").name("Cpt.Danko")
                .build();
        mockMvc.perform(post("/api/auth/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userInfoGoogleJacksonTester.write(googleUserInfoRequest).getJson()))
                .andExpect(status().isBadRequest());

        googleUserInfoRequest = GoogleUserInfoRequest.builder()
                .email("b@mydaytodo.com").pictureLink("/picture").name("")
                .build();

        mockMvc.perform(post("/api/auth/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userInfoGoogleJacksonTester.write(googleUserInfoRequest).getJson()))
                .andExpect(status().isBadRequest());
    }


}
