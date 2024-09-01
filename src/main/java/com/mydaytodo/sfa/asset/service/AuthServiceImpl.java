package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    public ServiceResponse authenticateUser(AuthRequest authRequest) {
        log.info("Received request to authenticate user");
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                                                    authRequest.getPassword()));

        if(authentication.isAuthenticated()) {
            log.info("User {} successfully authenticated", authRequest.getUsername());
            @SuppressWarnings("unchecked")
            Optional<FileUser> user = (Optional<FileUser>) userService.getByUsername(authRequest.getUsername()).getData();
            assert user.isPresent();
            String accessToken = jwtService.generateToken(authRequest.getUsername());
            LoginResponse resp =  LoginResponse.builder()
                    .username(user.get().getUsername())
                    .accessToken(accessToken)
                    .name(user.get().getName())
                    .build();
            return ServiceResponse.builder()
                    .isError(false)
                    .data(resp)
                    .status(HttpStatus.OK.value())
                    .build();
        } else {
            log.info("Authentication NOT successful");
            throw new UsernameNotFoundException("invalid user request");
        }
    }

    public ServiceResponse loginViaGoogle(@RequestBody @Valid GoogleUserInfoRequest googleUserInfoRequest) {
        log.info("User logged in on client site with Google credentials");
        // convert the object to a user request object
        CreateUserRequest createUserRequest = GoogleUserInfoRequest.convertToCreateUserRequest(googleUserInfoRequest);
        log.info("About to save user in DynamoDB");
        ServiceResponse response = userService.saveUser(createUserRequest);
        log.info("Save response {}", response.toString());
        if(response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return response;
        }

        String token = jwtService.generateToken(createUserRequest.getUsername());
        TokenResponse tokenResponse = TokenResponse.builder().accessToken(token).build();
        return ServiceResponse.builder()
                .data(tokenResponse)
                .message("")
                .status(HttpStatus.OK.value())
                .build();
    }
}
