package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Builder
public class ScheduleRepositoryLegacyImpl {

    private AmazonDynamoDB dynamoDB;
    @Autowired
    private AWSConfig awsConfig;

    private DynamoDBMapper dynamoDBMapper = null;

    @PostConstruct
    public void initalizeDB() {
        dynamoDB = awsConfig.amazonDynamoDB();
        dynamoDBMapper = new DynamoDBMapper(dynamoDB);
        log.info("Initialised dynamoDB class in ScheduleRepositoryImpl");
    }

    /**
     * Returns an entry object i.e. a tuple
     * if key is null, the request was successful
     * if not, then an error occured and the message is the key
     * @param schedule
     * @return
     */
    public Map.Entry<String, Schedule> createSchedule(Schedule schedule) {
        try {
            dynamoDBMapper.save(schedule);
        } catch (UnsupportedOperationException uoe) {
            log.info("Unsupported operation error");
            log.info(uoe.getLocalizedMessage());
            log.error(uoe.getMessage());
            return new AbstractMap.SimpleEntry<>(uoe.getMessage(), null);
        }
        log.info("schedule [ {} ] successfully saved", schedule.toString());
        return new AbstractMap.SimpleEntry<>(null, schedule);
    }
    public void deleteSchedule(String id) {
        log.info("About to delete schedule");
        DeleteItemRequest deleteItemRequest = new DeleteItemRequest();
        deleteItemRequest.setTableName("Schedule");
        deleteItemRequest.addKeyEntry("id", new AttributeValue().withS(id));
        Schedule schedule = getSchedule(id);
        dynamoDB.deleteItem(deleteItemRequest);
        log.info("schedule successfully deleted");
    }
    private Schedule getSchedule(String id) {
        return new Schedule();
    }
    // retrieve a list of schedules by user
    public List<Schedule> getUserSchedule(String userId) {
        return null;
    }

    private ServiceResponse updateSchedule(String userId, String scheduleId) {
        return null;
    }
}
