package com.mydaytodo.sfa.asset.model;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomCreateBucketRequest {
    private String bucketName;
    private String region;
}
