package com.mydaytodo.sfa.asset.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDBTable(tableName = "FileUser")
@ToString
public class FileUser {
    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBHashKey(attributeName = "user_id")
    private String userid;
    // to ensure uniqueness, the username is the email id
    @DynamoDBAttribute(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "password")
    private String password;
    @DynamoDBAttribute(attributeName = "date_joined")
    private Date dateJoined;
    @DynamoDBAttribute(attributeName = "last_login")
    private Date lastLogin;

    // @DynamoDBAttribute(attributeName = "email")
    // private String email;
    // @DynamoDBFlattened
    // maybe in the start, leave it to a "," separated string?
    //Use Lists and not a String[] array. Array types not supported by DynamoDB
    @DynamoDBAttribute(attributeName = "files_uploaded")
    private List<String> filesUploaded;

}
