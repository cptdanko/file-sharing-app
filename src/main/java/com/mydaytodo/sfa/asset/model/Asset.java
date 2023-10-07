package com.mydaytodo.sfa.asset.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private String name;
    private String id;
    private String keyStorePath;



}
