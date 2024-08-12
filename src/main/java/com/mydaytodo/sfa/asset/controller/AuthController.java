package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.*;
import com.mydaytodo.sfa.asset.service.JwtService;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth controller")
@Slf4j
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @PostMapping(value = "/login", consumes = {"application/json"})
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        log.info("Received login request for {}", authRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        log.info("Auth object {}", Boolean.toString(authentication.isAuthenticated()));
        if (authentication.isAuthenticated()) {
            log.info("Authentication successful");
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            log.info("Authentication NOT successful");
            throw new UsernameNotFoundException("invalid user request");
        }
    }
    @PostMapping(value = "/login/google", consumes = { "application/json" })
    public ResponseEntity<Object> googleLogin(@RequestBody @Valid UserInfoGoogle userInfoGoogle) {
        log.info("Logged in via Google, obtained user details , {}", userInfoGoogle.toString());
        // convert the object to a user request object
        CreateUserRequest createUserRequest = UserInfoGoogle.convertToCreateUserRequest(userInfoGoogle);
        log.info("About to save user in DynamoDB");
        ServiceResponse response = userService.saveUser(createUserRequest);
        log.info("Save response {}", response.toString());
        // if it's a conflict of username already exists in DB, that's ok
        // don't fail the request, simply create and send a new JWT token
        if(response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        }

        String token = jwtService.generateToken(createUserRequest.getUsername());
        return new ResponseEntity<>(TokenResponse.builder().accessToken(token).build(),
                HttpStatus.valueOf(response.getStatus()));
    }
}
