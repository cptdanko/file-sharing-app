package com.mydaytodo.sfa.asset.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDBTable(tableName = "AssetUser")
public class User {
    public enum ROLE {
        ADMIN,
        OWNER,
    }
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBHashKey(attributeName = "user_id")
    private String userid;
    // this could be another class and it's own table
    @DynamoDBAttribute(attributeName = "department")
    private String department;
    // post mvp, make this it's own table
    @DynamoDBAttribute(attributeName = "role")
    private String role;
    @DynamoDBAttribute(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "password")
    private String password;
    @DynamoDBAttribute(attributeName = "date_joined")
    private Date dateJoined;
    @DynamoDBAttribute(attributeName = "last_login")
    private Date lastLogin;
    // maybe in the start, leave it to a "," separated string?
    @DynamoDBAttribute(attributeName = "assets_uploaded")
    private String[] assetsUploaded;

    public void setRole(ROLE role) {
        this.role = role.toString();
    }
}
