package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ScheduleRepositoryImpl implements CustomScheduleRepository<Schedule, String> {
    @Autowired
    private com.mydaytodo.sfa.asset.config.AWSConfig AWSConfig;
    private DynamoDBMapper mapper = null;


    @PostConstruct
    private void initialiseDB() {
        AmazonDynamoDB dynamoDB = AWSConfig.amazonDynamoDB();
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
}
