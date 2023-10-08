package com.mydaytodo.sfa.asset.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Integer status;
    private Object data;
}
