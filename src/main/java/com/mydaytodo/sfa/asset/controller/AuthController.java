package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.*;
import com.mydaytodo.sfa.asset.service.AuthServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: refactor and move the logic in the controller to
 * service layer e.g. AuthService
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth controller")
@Slf4j
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping(value = "/login", consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> authenticateAndGetToken(@RequestBody @Valid AuthRequest authRequest) {
        log.info("Received login request for {}", authRequest.getUsername());
        ServiceResponse response = authService.authenticateUser(authRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    @PostMapping(value = "/login/google", consumes = { "application/json" })
    public ResponseEntity<Object> googleLogin(@RequestBody @Valid GoogleUserInfoRequest googleUserInfoRequest) {
        log.info("Logged in via Google, obtained user details , {}", googleUserInfoRequest.toString());
        // convert the object to a user request object
        ServiceResponse response = authService.loginViaGoogle(googleUserInfoRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
