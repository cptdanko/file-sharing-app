package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.mydaytodo.sfa.asset.config.DynamoDBConfig;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.AssetUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

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

    public AssetUser updateUser(String userId, AssetUser user) throws Exception {
        log.info("in updateUser() - "+ userId);
        AssetUser assetUser = getUser(userId);
        if(assetUser == null) {
            return null;
        }
        log.info("Retrieved assetUser");
        log.info(assetUser.getUsername());
        log.info(assetUser.getUserid());

        log.info("About to create dynamoDB expression");
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                .withExpectedEntry("used_id",
                        new ExpectedAttributeValue().withValue(new AttributeValue(userId)));
        mapper.save(assetUser, saveExpression);
        log.info("Saved the ddb obj");
        return assetUser;
    }
}
