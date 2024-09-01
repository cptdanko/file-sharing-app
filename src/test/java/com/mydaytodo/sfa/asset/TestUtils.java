package com.mydaytodo.sfa.asset;

import com.mydaytodo.sfa.asset.model.ServiceResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class TestUtils {

    public final static ServiceResponse successResponse = ServiceResponse.builder()
            .status(HttpStatus.OK.value())
            .build();

    public final static ServiceResponse createdResponse = ServiceResponse.builder()
            // 201
            .status(HttpStatus.CREATED.value())
            .build();

    public final static ServiceResponse noContentResponse = ServiceResponse.builder()
            // 201
            .status(HttpStatus.NO_CONTENT.value())
            .build();

    public final static ServiceResponse forbiddenResponse = ServiceResponse.builder()
            // 201
            .status(HttpStatus.FORBIDDEN.value())
            .build();
    public final static ServiceResponse internalServerResponse = ServiceResponse.builder()
            // 201
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();
}
