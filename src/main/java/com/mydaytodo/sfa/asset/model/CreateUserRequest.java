package com.mydaytodo.sfa.asset.model;

import com.amazonaws.util.StringUtils;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class CreateUserRequest {

    private String name;

    private String userId = "";
    private String username;
    private Date dateJoined = null;
    private List<String> filesUploaded;
    private String password;

    public static FileUser convertRequest(CreateUserRequest request) {
        FileUser fileUser = FileUser.builder()
                .name(request.getName())
                .userid(StringUtils.isNullOrEmpty(request.getUserId()) ? "" : request.getUserId())
                .dateJoined(request.getDateJoined())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        if (request.getFilesUploaded() != null) {
            fileUser.setFilesUploaded(request.getFilesUploaded());
        }
        log.info(String.format("About to save fileUser obj [ %s ]", fileUser.toString()));
        return fileUser;
    }
}
