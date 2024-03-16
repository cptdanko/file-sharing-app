package com.mydaytodo.sfa.asset.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Integer status;
    private Object data;
    private String message = "";
}
