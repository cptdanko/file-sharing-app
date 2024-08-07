package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.AuthRequest;
import com.mydaytodo.sfa.asset.service.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
