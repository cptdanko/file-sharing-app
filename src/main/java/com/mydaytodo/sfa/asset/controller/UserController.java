package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.User;
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
    public ResponseEntity<User> getUser(@RequestParam("userId") String userId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/")
    public ResponseEntity<HttpStatus> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@RequestParam("userId") String userId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<HttpStatus> updateUserDetails(@RequestParam("userId") String userId, @RequestBody CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Get all users by department
     * @param department
     * @return
     */
    @GetMapping("/all/{department}")
    public ResponseEntity<List<User>> getAllUserBy(@RequestParam("department") String department) {
        List<User> users = new ArrayList<>();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
