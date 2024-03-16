package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.FileUser;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ServiceResponse> getUser(@PathVariable("userId") String userId) {
        log.info("About to query user with id " + userId);
        ServiceResponse response = userService.getUser(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info(String.format("Got request [ %s ]", createUserRequest.toString()));
        ServiceResponse response = userService.saveUser(createUserRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ServiceResponse> deleteUser(@PathVariable("userId") String userId) {
        ServiceResponse deleteState = userService.deleteUser(userId);
        return new ResponseEntity<>(deleteState, HttpStatus.valueOf(deleteState.getStatus()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ServiceResponse> updateUserDetails(@PathVariable("userId") String userId, @RequestBody CreateUserRequest createUserRequest) {
        ServiceResponse response = userService.updateUser(userId, createUserRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
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
    public ResponseEntity<ServiceResponse> getByUsername(@RequestParam("username") String username) {
        ServiceResponse response = userService.getByUsername(username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}
