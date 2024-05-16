package com.mydaytodo.sfa.asset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareFileRequest {
    private String filename;
    private String fromUsername;
    private String toUsername;
}