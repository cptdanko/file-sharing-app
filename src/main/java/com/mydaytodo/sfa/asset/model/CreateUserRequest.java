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
    private String role = "";

    private String name;

    private String userId = "";
    private String username;
    private Date dateJoined = null;
    private List<String> assetsUploaded = new ArrayList<>();
    private String password;
    private String department = "";

    public static FileUser convertRequest(CreateUserRequest request) {
        return FileUser.builder()
                .role(request.getRole())
                .name(request.getName())
                .filesUploaded(request.getAssetsUploaded())
                .userid(StringUtils.isNullOrEmpty(request.getUserId()) ? "" : request.getUserId())
                .department(request.getDepartment())
                .dateJoined(request.getDateJoined())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

}
