package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.FileUser;
import com.mydaytodo.sfa.asset.service.UserAuthServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl {

    private AmazonDynamoDB dynamoDB = null;

    @Autowired
    private AWSConfig AWSConfig;
    private DynamoDBMapper mapper = null;

    @PostConstruct
    private void initializeDB() {
        dynamoDB = AWSConfig.amazonDynamoDB();
        mapper = new DynamoDBMapper(dynamoDB);
        log.info("In the init DB method of UserRepositoryImpl");
        initLoadUsers();
    }

    public Optional<FileUser> getUserByUsername(String username) throws Exception {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":username", new AttributeValue().withS(username));
        DynamoDBQueryExpression<FileUser> queryExp = new DynamoDBQueryExpression<FileUser>()
                .withIndexName("username-index")
                .withKeyConditionExpression("username= :username")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);
        return mapper.query(FileUser.class, queryExp)
                .stream()
                .findFirst();
    }

    public FileUser getUser(String id) throws Exception {
        return mapper.load(FileUser.class, id);
    }

    public void initLoadUsers() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withConsistentRead(true);

        List<FileUser> aUsers = new ArrayList<>(mapper.scan(FileUser.class, scanExpression));
        for (FileUser user : aUsers) {
            UserAuthServiceImpl.instance.addUser(user);
        }
    }

    /**
     * @param createUserRequest
     * @return
     */
    public FileUser saveUser(CreateUserRequest createUserRequest) throws Exception {
        log.info("In the save user method");
        FileUser user = CreateUserRequest.convertRequest(createUserRequest);
        user.setDateJoined(new Date());
        user.setLastLogin(new Date());
        user.setPassword(createUserRequest.getPassword());
        mapper.save(user);
        log.info("Request with name " + createUserRequest.getName() + " transformed");
        log.info("User saved");
        return user;
    }

    public Integer deleteUser(String userId) throws Exception {
        DeleteItemRequest request = new DeleteItemRequest();
        request.setTableName("AssetUser");
        request.addKeyEntry("user_id", new AttributeValue().withS(userId));
        FileUser assetUser = getUser(userId);
        if (assetUser == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        dynamoDB.deleteItem(request);
        return HttpStatus.NO_CONTENT.value();
    }
    public FileUser addFilenameToFilesUploaded(String userId, List<String> filenames) throws Exception {
        log.info("In the addfilenamesToFilesUploaded method");
        FileUser assetUser = getUser(userId);
        if (assetUser == null) {
            return null;
        }
        log.info("2");
        Map<String, AttributeValue> itemKey = new HashMap<>();
        UpdateItemRequest request = new UpdateItemRequest();
        request.setTableName("FileUser");
        itemKey.put("user_id", new AttributeValue().withS(userId));
        request.setKey(itemKey);
        log.info("3");
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        List<AttributeValue> attributeValues = new ArrayList<>();
        for (String doc : filenames) {
            attributeValues.add(new AttributeValue().withS(doc));
        }
        log.info(String.format("Total no of attribute value added [ %d ]", attributeValues.size()));
        updatedValues.put("files_uploaded", new AttributeValueUpdate().withValue(new AttributeValue().withL(attributeValues)));
        request.setAttributeUpdates(updatedValues);
        try {
            log.info("About to update item");
            dynamoDB.updateItem(request);
        } catch (Exception e) {
            log.info("Exception occured in the update command");
            log.error(e.getMessage());
            return null;
        }
        log.info("Saved the ddb obj");
        return null;
    }
    /**
     * @param userId
     * @param user
     * @return
     * @throws Exception
     */
    public FileUser updateUser(String userId, FileUser user) throws Exception {
        log.info("in updateUser() - " + userId);
        Map<String, AttributeValue> itemKey = new HashMap<>();
        /*  assetUser = getUser(userId);
        if (assetUser == null) {
            return null;
        }*/
        log.info("Retrieved assetUser");

        UpdateItemRequest request = new UpdateItemRequest();
        request.setTableName("FileUser");
        itemKey.put("user_id", new AttributeValue().withS(userId));
        request.setKey(itemKey);
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put("username", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getUsername())));
        updatedValues.put("name", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getName())));
        updatedValues.put("password", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getPassword())));
        // updatedValues.put("password", new AttributeValue())
        // TODO BUG: date str is not being saved in a consistent format, TO DO FIX
        /*if(user.getDateJoined() != null)
            updatedValues.put("date_joined", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getDateJoined().toString())));
        if(user.getLastLogin() != null)
            updatedValues.put("last_login", new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getLastLogin().toString())));*/

        List<AttributeValue> attributeValues = new ArrayList<>();
        for (String doc : user.getFilesUploaded()) {
            attributeValues.add(new AttributeValue().withS(doc));
        }
        log.info(String.format("Total no of attribute value added [ %d ]", attributeValues.size()));
        updatedValues.put("files_uploaded", new AttributeValueUpdate().withValue(new AttributeValue().withL(attributeValues)));

        request.setAttributeUpdates(updatedValues);
        try {
            dynamoDB.updateItem(request);
        } catch (Exception e) {
            log.info("Exception occured in the update command");
            log.error(e.getMessage());
            return null;
        }
        log.info("Saved the ddb obj");
        return null;
        // return assetUser;
    }

}
