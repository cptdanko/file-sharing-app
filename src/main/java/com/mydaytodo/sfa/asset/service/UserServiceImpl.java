package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.AssetUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl {
    @Autowired
    private UserRepositoryImpl userRepository;

    public ServiceResponse getUser(String id) {
        AssetUser user = null;
        try {
            log.info("About to query userRepository with id " + id);
            user = userRepository.getUser(id);
            if (user == null) {
                log.info("Got response");
                return ServiceResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .data(null)
                        .build();
            }
            return ServiceResponse.builder()
                    .status(HttpStatus.OK.value())
                    .data(user)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(null)
                    .message(e.getLocalizedMessage())
                    .build();
        }
    }

    public ServiceResponse getByUsername(String username) {
        Optional<AssetUser> user;
        log.info("About to get user by username");
        try {
            user = userRepository.getUserByUsername(username);
            if (user.isPresent()) {
                return ServiceResponse.builder()
                        .data(user)
                        .status(HttpStatus.OK.value())
                        .message("User found")
                        .build();
            } else {
                return ServiceResponse.builder()
                        .message("User with username " + username + " not found. Are you sure it's the right name?")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    public ServiceResponse saveUser(CreateUserRequest userRequest) {
        log.info(String.format("About to print out user data [ %s ]", userRequest.toString()));
        String key = KeyStart.USER_KEY + System.currentTimeMillis();
        userRequest.setUserId(key);
        log.info("About to save user with name " + userRequest.getUsername());
        try {
            // add validation to ensure user does not exist
            if (userRepository.getUserByUsername(userRequest.getUsername()).isPresent()) {
                return ServiceResponse.builder()
                        .data(null)
                        .message(userRequest.getUsername() + " already exists in database")
                        .status(HttpStatus.CONFLICT.value())
                        .build();
            }
            userRequest.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
            AssetUser createdUser = userRepository.saveUser(userRequest);
            log.info(createdUser.toString());
            log.info("Now about to add user to basic auth store");
            UserAuthServiceImpl.instance.addUser(createdUser);
            // CustomUserService.instance.getInMemoryUserDetailsManager().
            return ServiceResponse.builder()
                    .message("")
                    .status(HttpStatus.CREATED.value())
                    .data(createdUser)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(null)
                    .build();
        }
    }

    public ServiceResponse deleteUser(String userId) {
        try {
            Integer responseCode = userRepository.deleteUser(userId);
            return ServiceResponse.builder()
                    .data(null)
                    .status(responseCode)
                    .message("Delete successful")
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getLocalizedMessage())
                    .build();
        }
    }

    public ServiceResponse updateUser(String userId, CreateUserRequest createUserRequest) {
        try {
            AssetUser assetUser = CreateUserRequest.convertRequest(createUserRequest);
            assetUser = userRepository.updateUser(userId, assetUser);
            return ServiceResponse.builder()
                    .data(assetUser)
                    .status(HttpStatus.NO_CONTENT.value())
                    .message("")
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getLocalizedMessage())
                    .build();
        }
    }
}
