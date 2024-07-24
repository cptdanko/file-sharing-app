package com.mydaytodo.sfa.asset.error;

import com.mydaytodo.sfa.asset.model.ServiceResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice()
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ServiceResponse> handleJwtExpiryException(JwtException jwtException, WebRequest request) {
        log.info("Handling an expired JWT exception");
        ServiceResponse resp = ServiceResponse.builder()
                .data(String.format("JWT token expired!!!!!!!!!! need to login again"))
                .message(jwtException.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .build();
        ResponseEntity<ServiceResponse> res = new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
        return res;
        // return handleExceptionInternal(jwtException, res, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
