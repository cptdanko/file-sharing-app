package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.*;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import com.mydaytodo.sfa.asset.service.JwtService;
import com.mydaytodo.sfa.asset.service.MailService;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/{userId}")
    public ResponseEntity<ServiceResponse> getUser(@PathVariable("userId") String userId) {
        log.info("About to query user with id {}", userId);
        ServiceResponse response = userService.getUser(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PostMapping(value = "/create", consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("Got request [ {} ]", createUserRequest.toString());
        ServiceResponse response = userService.saveUser(createUserRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ServiceResponse> deleteUser(@PathVariable("userId") String userId) {
        ServiceResponse deleteState = userService.deleteUser(userId);
        return new ResponseEntity<>(deleteState, HttpStatus.valueOf(deleteState.getStatus()));
    }

    @PutMapping(value = "/{userId}", consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> updateUserDetails(@PathVariable("userId") String userId,
                                                             @RequestBody CreateUserRequest createUserRequest) {
        ServiceResponse response = userService.updateUser(userId, createUserRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * Endpoint to list which files the user has access to
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/files")
    public ResponseEntity<ServiceResponse> getFilesAccessibleToUser(@RequestParam("username") String username) {
        Validator.validateUsernameAndToken(username);
        ServiceResponse response = userService.getFilesAccessibleToUser(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all users by department
     *
     * @param department
     * @return
     */
    @GetMapping("/all/{department}")
    public ResponseEntity<List<FileUser>> getAllUserBy(@RequestParam("department") String department) {
        List<FileUser> users = new ArrayList<>();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/username")
    public ResponseEntity<ServiceResponse> getByUsername(@RequestParam("username") String username) throws UsernameNotFoundException{
        // a user can only see their own details
        // check for user role to be added later i.e. admin can see everything
        Validator.validateUsernameAndToken(username);
        ServiceResponse response = userService.getByUsername(username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /*
     * File sharing algorithm
     * A user can share a file owned
     * A user who doesn't own only has read access, no share
     * NOT USED RIGHT NOW - THIS ENDPOINT WAS ADDED PREMATURELY
     * WITHOUT THINKING THROUGH THE EVERYTHING THE FEATURE WOULD
     * INVOLVE E.G. it makes sense to add this when social features
     * such as follower, friend or acquaintance is added to the app
     */
    @PostMapping(value = "/share", consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> postShareResponse(@RequestBody ShareFileRequest shareFileRequest)
            throws Exception {
        log.info(String.format("Share file request received for %s", shareFileRequest.toString()));
        // for now just return Http.OK
        userService.shareFile(shareFileRequest.getFromUsername(), shareFileRequest.getToUsername(),
                shareFileRequest.getFilename());
        return new ResponseEntity<>(new ServiceResponse(), HttpStatus.OK);
    }

    @GetMapping(value = "/all/{username}")
    public ResponseEntity<ServiceResponse> getAllUsersFor(@PathVariable("username") String username) {
        log.info("Received request to get all users logged in user can share with");
        // userService.getAllUsersFor(username);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping(value = "/sendMail")
    public ResponseEntity<ServiceResponse> sendMail(@RequestBody EmailRequest emailRequest) {
        if(fileService.validateFileType(emailRequest.getFilesToAttach()).getStatus() != null) {
            ServiceResponse response = fileService.validateFileType(emailRequest.getFilesToAttach());
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        }

        ServiceResponse serviceResponse = mailService.sendEmail(emailRequest);
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatus()));
    }

    @PostMapping(value = "/login", consumes = {"application/json"})
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        log.info("Called generateToken API and about to generate authenticate via authentication manager");
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
