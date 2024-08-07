package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.FileUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@ToString
@Service
@Slf4j
public class UserServiceImpl {

    @Autowired
    private UserRepositoryImpl userRepository;

    public ServiceResponse getUser(String id) {
        FileUser user = null;
        try {
            log.info("About to query userRepository with id {}", id);
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
        Optional<FileUser> user;
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
    public ServiceResponse getFilesAccessibleToUser(String username) {
        log.info("In getFilesAccessibleToUser method");
        try {
            // we don't expect this to be null anyway
            FileUser user = userRepository.getUserByUsername(username).orElseThrow();
            log.info("These [ {} ] files are accessible to user", user.getFilesUploaded());
            return ServiceResponse.builder()
                    .data(user.getFilesUploaded())
                    .status(HttpStatus.OK.value())
                    .message("User found")
                    .build();
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
            Map.Entry<String, FileUser> entry = userRepository.saveUser(userRequest);
            if(entry.getKey() != null) {
                return ServiceResponse.builder()
                        .data(null)
                        .message(entry.getKey())
                        .build();
            }
            FileUser createdUser = entry.getValue();
            log.info("User created {}", entry.toString());
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

    /**
     * ins
     * 
     * @param userId
     * @param createUserRequest
     * @return
     */
    public ServiceResponse updateUser(String userId, CreateUserRequest createUserRequest) {
        try {
            log.info(String.format("CreateUserRequest body received [ %s ]", createUserRequest.toString()));
            FileUser assetUser = CreateUserRequest.convertRequest(createUserRequest);
            log.info(String.format("Got the file user obj [ %s ]", assetUser.toString()));
            userRepository.updateUser(userId, assetUser);
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

    /**
     * as a later update, add rules to check
     * whether or not the file can be shared by the user e.g.
     * something like only the user who uploaded the file
     * can share it or so
     *
     * @throws Exception
     */
    public ServiceResponse shareFile(String from, String to, String filename) throws Exception {
        log.info("Received a request to share file, {}", filename);
        String filepath = from + "/" + filename;
        log.info("{} about to share file {} with {}", from, filepath, to);
        addFIlenameToFilesUploadedByUser(to, filepath);
        return new ServiceResponse().builder()
                .data(new Object()).status(HttpStatus.OK.value())
                .message("Successful")
                .build();
    }

    /**
     * @param username
     * @param filename
     * @throws Exception
     */
    public void addFIlenameToFilesUploadedByUser(String username, String filename) throws Exception {
        log.info("About to add the filename to files uploaded");
        Optional<FileUser> optionalUser = userRepository.getUserByUsername(username);
        FileUser user = optionalUser.get();
        log.info("Got user by name [ {} ]", user);
        log.info("About to get files uploaded");
        List<String> fileList = user.getFilesUploaded();
        if (fileList == null) {
            fileList = new ArrayList<>();
            user.setFilesUploaded(fileList);
        }
        log.info("The number of files uploaded by user are {}", fileList.size());
        fileList.add(filename);
        userRepository.addFilenameToFilesUploaded(user.getUserid(), fileList);
        log.info("Updated user object [ {}]", user.getFilesUploaded().size());
    }
    public void deleteFilenameFromFilesUploaded(String filename, String username) {
        log.info("Got request to delete file {} by user {}", filename, username);
        FileUser user = userRepository.getUserByUsername(username).orElseThrow();
        log.info("No of files before deleted {}", user.getFilesUploaded().size());

        log.info("name {}",filename);
        log.info("username {}", username);

        List<String> filenames = user.getFilesUploaded().stream()
                .filter(name -> !name.equalsIgnoreCase(String.format("%s/%s",username, filename)))
                .collect(Collectors.toList());
        user.setFilesUploaded(filenames);
        log.info("No of files after deleted {}", user.getFilesUploaded().size());
        log.info("About to update the user object with total no of files changed");
        userRepository.updateUser(user.getUserid(), user);
    }
}
