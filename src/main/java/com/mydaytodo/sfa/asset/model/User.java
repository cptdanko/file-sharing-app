package com.mydaytodo.sfa.asset.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    public enum ROLE {
        ADMIN,
        OWNER,
    }
    private String name;
    private String userid;
    // this could be another class and it's own table
    private String department;
    // post mvp, make this it's own table
    private String role;
    private String username;
    private String password;
    private Date dateJoined;
    private Date lastLogin;
    private List<String> assetsUploaded;

    public void setRole(ROLE role) {
        this.role = role.toString();
    }
}
