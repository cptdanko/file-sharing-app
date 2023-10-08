package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.mydaytodo.sfa.asset.config.DynamoDBConfig;
import com.mydaytodo.sfa.asset.model.Document;
import com.mydaytodo.sfa.asset.model.DocumentType;
import com.mydaytodo.sfa.asset.model.DocumentUploadRequest;
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
public class DocumentRepositoryImpl {
    private AmazonDynamoDB dynamoDB = null;

    @Autowired
    private DynamoDBConfig dynamoDBConfig;
    private DynamoDBMapper mapper = null;


    @PostConstruct
    private void initialiseDB() {
        dynamoDB = dynamoDBConfig.amazonDynamoDB();
        mapper = new DynamoDBMapper(dynamoDB);
    }
    public Document getDocument(String id) {
        try {
            return mapper.load(Document.class, id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public Integer saveAsset(DocumentUploadRequest request) {
        Integer retVal = HttpStatus.CREATED.value();
        log.info("In saveAsset method");
        Document asset = DocumentUploadRequest.convertRequest(request);
        log.info("Saving document metadata with name "+ asset.getName());
        mapper.save(asset);
        log.info("Saved asset metadata");
        return retVal;
    }
    public Integer deleteDocument(String id) {
        DeleteItemRequest request = new DeleteItemRequest();
        request.setTableName("Document");
        request.addKeyEntry("id", new AttributeValue().withS(id));
        Document document = getDocument(id);
        if(document == null) {
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

    public Integer updateDocument(String id, DocumentUploadRequest documentUploadRequest) {
        Document documentToUpdate = getDocument(id);
        if(documentToUpdate == null) {
            return HttpStatus.NOT_FOUND.value();
        }
        log.info("About to update document with id "+ documentUploadRequest.getId());
        documentToUpdate.transformForUpdate(documentUploadRequest);
        DynamoDBSaveExpression saveOp = new DynamoDBSaveExpression()
                .withExpectedEntry("id", new ExpectedAttributeValue().withValue(new AttributeValue(id)));
        mapper.save(documentToUpdate, saveOp);
        return org.apache.http.HttpStatus.SC_NO_CONTENT;
    }
    public List<Document> getUserDocuments(String userId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        Document document = Document
                            .builder()
                            .userId(userId)
                            .build();
        eav.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<Document> queryExp = new DynamoDBQueryExpression<Document>()
                .withIndexName("user_id-index")
                .withKeyConditionExpression("user_id= :userId")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return mapper.query(Document.class, queryExp);
    }

    /**
     * Note: there is a cost associated with dynamoDB indexes
     * Perhaps for mvp change this to scan the table to get
     * all values in the table, and filter in code, here
     * @param documentType
     * @return
     */
    public List<Document> getDocumentsOfType(DocumentType documentType) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":type", new AttributeValue().withS(documentType.getType()));
        DynamoDBQueryExpression<Document> queryExp = new DynamoDBQueryExpression<Document>()
                .withIndexName("asset_type-index")
                .withKeyConditionExpression("asset_type = :type")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);
        return mapper.query(Document.class, queryExp);

    }
}
