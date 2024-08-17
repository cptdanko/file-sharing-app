package com.mydaytodo.sfa.asset.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ServiceResponse {
    private Integer status;
    private Object data;
    private String message = "";
    private boolean isError = false;
}
