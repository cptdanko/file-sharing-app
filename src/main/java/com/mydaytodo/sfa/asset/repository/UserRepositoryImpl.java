package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.mydaytodo.sfa.asset.config.DynamoDBConfig;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.AssetUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class UserRepositoryImpl {
    private AmazonDynamoDB dynamoDB = null;

    @Autowired
    private DynamoDBConfig dynamoDBConfig;
    private DynamoDBMapper mapper = null;

    @PostConstruct
    private void initializeDB() {
        dynamoDB = dynamoDBConfig.amazonDynamoDB();
        mapper = new DynamoDBMapper(dynamoDB);
    }

    public AssetUser getUser(String id) throws Exception {
        return mapper.load(AssetUser.class, id);
    }

    /**
     * @param createUserRequest
     * @return
     */
    public AssetUser saveUser(CreateUserRequest createUserRequest) throws Exception {
        log.info("In the save user method");
        AssetUser user = CreateUserRequest.convertRequest(createUserRequest);
        user.setDateJoined(new Date());
        user.setLastLogin(new Date());
        log.info(user.getUsername());
        log.info(user.getUserid());
        mapper.save(user);
        log.info("Request with name "+ createUserRequest.getName() + " transformed");
        log.info("User saved");
        return user;
    }
    public Integer deleteUser(String userId) throws Exception {
        DeleteItemRequest request = new DeleteItemRequest();
        request.setTableName("AssetUser");
        request.addKeyEntry("user_id", new AttributeValue().withS(userId));
        AssetUser assetUser = getUser(userId);
        if(assetUser == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        dynamoDB.deleteItem(request);
        return HttpStatus.NO_CONTENT.value();
    }

    /**
     *         HashMap<String,AttributeValue> itemKey = new HashMap<>();
     *         itemKey.put(key, AttributeValue.builder()
     *             .s(keyVal)
     *             .build());
     *
     *          HashMap<String,AttributeValueUpdate> updatedValues = new HashMap<>();
     *         updatedValues.put(name, AttributeValueUpdate.builder()
     *             .value(AttributeValue.builder().s(updateVal).build())
     *             .action(AttributeAction.PUT)
     *             .build());
     *
     *         UpdateItemRequest request = UpdateItemRequest.builder()
     *             .tableName(tableName)
     *             .key(itemKey)
     *             .attributeUpdates(updatedValues)
     *             .build();
     *
     *         try {
     *             ddb.updateItem(request);
     *         } catch (ResourceNotFoundException e) {
     *             System.err.println(e.getMessage());
     *             System.exit(1);
     *         } catch (DynamoDbException e) {
     *             System.err.println(e.getMessage());
     *             System.exit(1);
     *         }
     *         System.out.println("The Amazon DynamoDB table was updated!");
     * @param userId
     * @param user
     * @return
     * @throws Exception
     */
    public AssetUser updateUser(String userId, AssetUser user) throws Exception {
        log.info("in updateUser() - "+ userId);
        Map<String, AttributeValue> itemKey = new HashMap<>();
        log.info("RAN THE UPDATE COMMAND");
        AssetUser assetUser = getUser(userId);
        if(assetUser == null) {
            return null;
        }
        log.info("Retrieved assetUser");

        UpdateItemRequest request = new UpdateItemRequest();
        request.setTableName("AssetUser");
        itemKey.put("user_id", new AttributeValue().withS(userId));
        request.setKey(itemKey);
        HashMap<String,AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put("username", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getUsername())));
        updatedValues.put("name", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getName())));
        updatedValues.put("department", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getDepartment())));
        updatedValues.put("role", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getRole())));
        if(user.getDateJoined() != null)
            updatedValues.put("date_joined", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getDateJoined().toString())));
        if(user.getLastLogin() != null)
            updatedValues.put("last_login", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getLastLogin().toString())));

        updatedValues.put("assets_uploaded", new AttributeValueUpdate().withValue(new AttributeValue().withSS(user.getAssetsUploaded())));
        request.setAttributeUpdates(updatedValues);
        try {
            dynamoDB.updateItem(request);
        } catch (Exception e) {
            log.info("Exception occured in the update command");
            log.error(e.getMessage());
            return null;
        }
        log.info("Saved the ddb obj");
        return assetUser;
    }
}
