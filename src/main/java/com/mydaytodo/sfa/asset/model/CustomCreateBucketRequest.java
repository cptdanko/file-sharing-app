package com.mydaytodo.sfa.asset.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomCreateBucketRequest {
    private String bucketName;
    private String region;
}
