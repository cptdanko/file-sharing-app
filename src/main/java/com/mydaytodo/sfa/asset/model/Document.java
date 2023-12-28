package com.mydaytodo.sfa.asset.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Document")
public class Document {

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "key_store_path")
    private String keyStorePath;

    @DynamoDBAttribute(attributeName = "asset_type")
    private String assetType;

    @DynamoDBAttribute(attributeName = "user_id")
    private String userId;

    public void transformForUpdate(DocumentMetadataUploadRequest documentMetadataUploadRequest) {
        setName(documentMetadataUploadRequest.getName());
        setAssetType(documentMetadataUploadRequest.getAssetType());
        setUserId(documentMetadataUploadRequest.getUserId());
        setKeyStorePath(documentMetadataUploadRequest.getPath());
    }
}
