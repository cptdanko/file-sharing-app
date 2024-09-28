package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.db.FileUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceImplTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;
    
    private final FileUser mockUser = FileUser.builder()
            .username("bhuman@mydaytodo.com")
            .dateJoined(new Date())
            .password(new BCryptPasswordEncoder().encode("1234"))
            .name("Bhuman Soni")
            .filesUploaded(new ArrayList<>())
            .build();

    @Test
    void testLoadByUsername_fail() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            when(userRepository.getUserByUsername(any())).thenReturn(Optional.empty());
            userAuthService.loadUserByUsername(any());
            Mockito.verify(userRepository, times(1)).getUserByUsername(any());
        });
    }
    @Test
    void testLoadByUsername_success() {
        when(userRepository.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        UserDetails userDetails = userAuthService.loadUserByUsername(any());
        Assertions.assertEquals("bhuman@mydaytodo.com", userDetails.getUsername());
        Assertions.assertFalse(userDetails.getPassword().isEmpty());
    }
    @Test
    void testLoadByUsername_success_2() {
        mockUser.setIsSocialLoginGoogle(true);
        when(userRepository.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        UserDetails userDetails = userAuthService.loadUserByUsername(any());
        Assertions.assertEquals("", userDetails.getPassword());
    }
}
