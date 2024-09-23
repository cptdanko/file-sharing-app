package com.mydaytodo.sfa.asset.service;

import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.yml")
public class JwtServiceTest {

    @Autowired
    private final JwtService jwtService = new JwtService();

    @Test
    public void testGenerateToken_error() {
        Map<String, Object> mockClaims = new HashMap<>();
        // get mock claims
        Assertions.assertThrows(WeakKeyException.class, () -> {
            String randomSmallKey = "SOMERANDOMKEY1234556";
            ReflectionTestUtils.setField(jwtService, "SECRET", randomSmallKey);
            jwtService.generateToken("bhuman.soni@gmail.com");
        });
    }
    @Test
    public void testGenerateToken_success() {
        Map<String, Object> mockClaims = new HashMap<>();
        // get mock claims
        String randomSmallKey = "SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY12";
        ReflectionTestUtils.setField(jwtService, "SECRET", randomSmallKey);
        String token = jwtService.generateToken("bhuman.soni@gmail.com");
        Assertions.assertFalse(token.isEmpty());
    }
    @Test
    public void testextractUsername_success() {
        Map<String, Object> mockClaims = new HashMap<>();
        // get mock claims
        String username ="bhuman.soni@gmail.com";
        String randomSmallKey = "SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY12";
        ReflectionTestUtils.setField(jwtService, "SECRET", randomSmallKey);
        String token = jwtService.generateToken("bhuman.soni@gmail.com");
        Assertions.assertEquals(jwtService.extractUsername(token), username);
    }
    @Test
    public void testextractAllClaimssuccess() {
        Map<String, Object> mockClaims = new HashMap<>();
        // get mock claims
        String username ="bhuman.soni@gmail.com";
        String randomSmallKey = "SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY12";
        ReflectionTestUtils.setField(jwtService, "SECRET", randomSmallKey);
        String token = jwtService.generateToken("bhuman.soni@gmail.com");
        Assertions.assertEquals(jwtService.extractEmail(token), "null");
    }
    @Test
    public void isTokenExpired() {
        Map<String, Object> mockClaims = new HashMap<>();
        // get mock claims
        String username ="bhuman.soni@gmail.com";
        String randomSmallKey = "SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY1234556SOMERANDOMKEY12";
        ReflectionTestUtils.setField(jwtService, "SECRET", randomSmallKey);
        String token = jwtService.generateToken("bhuman.soni@gmail.com");

        Assertions.assertEquals(jwtService.isTokenExpired(token), false);
    }

}
