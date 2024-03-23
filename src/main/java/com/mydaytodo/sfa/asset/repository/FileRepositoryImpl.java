package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.File;
import com.mydaytodo.sfa.asset.model.FileType;
import com.mydaytodo.sfa.asset.model.FileMetadataUploadRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FileRepositoryImpl {
    private AmazonDynamoDB dynamoDB = null;

    @Autowired
    private AWSConfig AWSConfig;
    private DynamoDBMapper mapper = null;


    @PostConstruct
    private void initialiseDB() {
        dynamoDB = AWSConfig.amazonDynamoDB();
        mapper = new DynamoDBMapper(dynamoDB);
    }
    public File getDocument(String id) {
        try {
            return mapper.load(File.class, id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public Integer saveFileMetadata(FileMetadataUploadRequest request) {
        Integer retVal = HttpStatus.CREATED.value();
        log.info("In saveAsset method");
        File asset = FileMetadataUploadRequest.convertRequest(request);
        log.info("Saving document metadata with name "+ asset.getName());
        mapper.save(asset);
        log.info("Saved asset metadata");
        return retVal;
    }
    public Integer deleteDocument(String id) {
        DeleteItemRequest request = new DeleteItemRequest();
        request.setTableName("Document");
        request.addKeyEntry("id", new AttributeValue().withS(id));
        File file = getDocument(id);
        if(file == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        try {
            dynamoDB.deleteItem(request);
            return HttpStatus.NO_CONTENT.value();
        } catch (Exception e) {
            log.info("Exception occured in deleting document");
            log.info("Document id ="+ id);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }

    public Integer updateDocument(String id, FileMetadataUploadRequest fileMetadataUploadRequest) {
        File fileToUpdate = getDocument(id);
        if(fileToUpdate == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        log.info("About to update document with id "+ fileMetadataUploadRequest.getId());
        fileToUpdate.transformForUpdate(fileMetadataUploadRequest);
        DynamoDBSaveExpression saveOp = new DynamoDBSaveExpression()
                .withExpectedEntry("id", new ExpectedAttributeValue().withValue(new AttributeValue(id)));
        mapper.save(fileToUpdate, saveOp);
        return org.apache.http.HttpStatus.SC_NO_CONTENT;
    }
    public List<File> getUserDocuments(String userId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        File file = File
                            .builder()
                            .userId(userId)
                            .build();
        eav.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<File> queryExp = new DynamoDBQueryExpression<File>()
                .withIndexName("user_id-index")
                .withKeyConditionExpression("user_id= :userId")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return mapper.query(File.class, queryExp);
    }

    /**
     * Note: there is a cost associated with dynamoDB indexes
     * Perhaps for mvp change this to scan the table to get
     * all values in the table, and filter in code, here
     * @param fileType
     * @return
     */
    public List<File> getDocumentsOfType(FileType fileType) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":type", new AttributeValue().withS(fileType.getType()));
        DynamoDBQueryExpression<File> queryExp = new DynamoDBQueryExpression<File>()
                .withIndexName("asset_type-index")
                .withKeyConditionExpression("asset_type = :type")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);
        return mapper.query(File.class, queryExp);

    }
}
