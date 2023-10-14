package com.mydaytodo.sfa.asset.model;

import com.amazonaws.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DocumentUploadRequest {

    private String name;
    private String path;
    private String assetType;
    private String id;
    private String userId;

    /**
     * @param uploadRequest
     * @return
     */
    public static Document convertRequest(DocumentUploadRequest uploadRequest) {
        return Document.builder()
                .keyStorePath(uploadRequest.getPath())
                .assetType(uploadRequest.getAssetType())
                .name(uploadRequest.getName())
                .id(StringUtils.isNullOrEmpty(uploadRequest.getId())? null: uploadRequest.getId())
                .userId(uploadRequest.getUserId())
                .build();
    }
}
