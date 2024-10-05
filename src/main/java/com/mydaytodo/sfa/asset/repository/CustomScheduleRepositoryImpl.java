package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

@Slf4j
@Component
public class CustomScheduleRepositoryImpl implements CustomScheduleRepository<Schedule, String> {
    @Autowired
    private com.mydaytodo.sfa.asset.config.AWSConfig AWSConfig;
    private DynamoDBMapper mapper = null;
    private AmazonDynamoDB dynamoDB = null;

    @PostConstruct
    private void initialiseDB() {
        dynamoDB = AWSConfig.amazonDynamoDB();
        mapper = new DynamoDBMapper(dynamoDB);
    }
    @Override
    public List<Schedule> getScheduleByUser(String username) {
        log.info("received Request to query user");
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":username", new AttributeValue().withS(username));
        log.info("Printing out the map -> {}", eav.toString());
        DynamoDBQueryExpression<Schedule> queryExp = new DynamoDBQueryExpression<Schedule>()
                .withIndexName("username-index")
                .withKeyConditionExpression("username = :username")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);
        log.info("Finished querying DynamoDB");
        return mapper.query(Schedule.class, queryExp);
    }

    @Override
    public void updateScheduleIsSent(String scheduleId, boolean isSent) {
        log.info("About to set schedule sent to{}", isSent);
        UpdateItemRequest request = new UpdateItemRequest();
        Map<String, AttributeValue> itemKey = new HashMap<>();
        request.setKey(itemKey);
        itemKey.put("id", new AttributeValue().withS(scheduleId));
        Map<String, AttributeValueUpdate> updateValues = new HashMap<>();
        request.setTableName("Schedule");
        updateValues.put("is_sent", new AttributeValueUpdate().withValue(
                new AttributeValue().withBOOL(isSent)));
        //updateValues.put("user_id", new AttributeValue().withS(scheduleId));
        request.setAttributeUpdates(updateValues);
        dynamoDB.updateItem(request);
    }
}
