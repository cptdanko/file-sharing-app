package com.mydaytodo.sfa.asset.model.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "Schedule")
@ToString
@Builder
public class Schedule {

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "username")
    private String username;

    @DynamoDBAttribute(attributeName = "sender")
    private String sender;

    @DynamoDBAttribute(attributeName = "receivers")
    private List<String> receivers;

    @DynamoDBAttribute(attributeName = "time_window")
    private String timeWindow;

    @DynamoDBAttribute(attributeName = "is_recurring")
    private boolean isRecurring;

    @DynamoDBAttribute(attributeName = "filename")
    private String filename;

    @DynamoDBAttribute(attributeName = "is_sent")
    private Boolean isSent;
}
