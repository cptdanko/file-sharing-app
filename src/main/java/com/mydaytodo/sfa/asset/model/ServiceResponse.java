package com.mydaytodo.sfa.asset.model;

import lombok.*;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Integer status;
    private Object data;
    private String message = "";
}
