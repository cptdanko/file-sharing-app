package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.AssetUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.ion.SeekableReader;

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
            if(user == null) {
                log.info("Got response");
                log.info(user.getName());
                log.info(user.getUserid());
                return ServiceResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .data(null)
                        .build();
            }
            return ServiceResponse.builder()
                    .status(HttpStatus.OK.value())
                    .data(user)
                    .build();
        } catch(Exception e) {
            return ServiceResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(null)
                    .message(e.getLocalizedMessage())
                    .build();
        }
    }

    public ServiceResponse saveUser(CreateUserRequest userRequest) {
        String key = KeyStart.USER_KEY +  System.currentTimeMillis();
        userRequest.setUserId(key);
        log.info(userRequest.getUsername());
        log.info(userRequest.getName());
        try {
            AssetUser createdUser = userRepository.saveUser(userRequest);
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
