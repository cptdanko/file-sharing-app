package com.mydaytodo.sfa.asset.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
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
@DynamoDBTable(tableName = "AssetUser")
@ToString
public class FileUser {
    @Getter
    enum ROLE {
        ADMIN("admin"),
        USER("user");

        private String role;
        ROLE(String role) {
            this.role = role;
        }
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
    // to ensure uniqueness, the username is the email id
    @DynamoDBAttribute(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "password")
    private String password;
    @DynamoDBAttribute(attributeName = "date_joined")
    private Date dateJoined;
    @DynamoDBAttribute(attributeName = "last_login")
    private Date lastLogin;

    @DynamoDBAttribute(attributeName = "email")
    private String email;
    // @DynamoDBFlattened
    // maybe in the start, leave it to a "," separated string?
    //Use Lists and not a String[] array. Array types not supported by DynamoDB
    @DynamoDBAttribute(attributeName = "assets_uploaded")
    private List<String> filesUploaded;

}
