package com.mydaytodo.sfa.asset.service;


import java.util.*;

import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.db.FileUser;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceTest {
    @Mock
    private UserRepositoryImpl userRepositoryImpl;

    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private final List<String> files = new ArrayList<>();
    private final String TEST_USER_ID = "USR_123";
    private final String MOCK_USERNAME = "bhuman@mydaytodo.com";

    private final CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .username(MOCK_USERNAME)
            .filesUploaded(new ArrayList<>())
            .isSocialLoginGoogle(false)
            .name("Bhuman")
            .profilePicLink("http://mydaytodo.com/blog")
            .password("1234")
            .build();

    private final FileUser mockUser = FileUser.builder()
        .username("bhuman@mydaytodo.com")
        .dateJoined(new Date())
        .password(new BCryptPasswordEncoder().encode("1234"))
        .name("Bhuman Soni")
        .filesUploaded(files)
        .build();

    @Test
    public void testGetUser_success() throws Exception {
        when(userRepositoryImpl.getUser(any())).thenReturn(mockUser);
        ServiceResponse response = userServiceImpl.getUser(TEST_USER_ID);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(((FileUser) response.getData()).getUsername(), mockUser.getUsername());
    }

    @Test
    public void testGetUser_notFound() throws Exception {
        when(userRepositoryImpl.getUser(any())).thenReturn(null);
        ServiceResponse response = userServiceImpl.getUser(TEST_USER_ID);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }
    @Test
    public void testGetByUsername_success() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        ServiceResponse response = userServiceImpl.getByUsername(MOCK_USERNAME);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void testGetByUsername_notFound() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.empty());
        ServiceResponse response = userServiceImpl.getByUsername(MOCK_USERNAME);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void testGetFilesAccessible_success() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        ServiceResponse response = userServiceImpl.getFilesAccessibleToUser(MOCK_USERNAME);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ArrayList.class, response.getData().getClass());
    }
    @Test
    public void testGetFilesAccessible_fail() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.empty());
        ServiceResponse response = userServiceImpl.getFilesAccessibleToUser(MOCK_USERNAME);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }
    @Test
    public void testSaveUser_success() {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.empty());
        when(userRepositoryImpl.saveUser(any())).thenReturn(new AbstractMap.SimpleEntry<>(null, mockUser));
        ServiceResponse response = userServiceImpl.saveUser(createUserRequest);
        assertEquals(((FileUser) response.getData()).getUsername(), mockUser.getUsername());
    }
    @Test
    public void testSaveUser_fail_userexists() {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.ofNullable(mockUser));
        ServiceResponse response = userServiceImpl.saveUser(createUserRequest);
        assertEquals(HttpStatus.SC_CONFLICT, response.getStatus());
    }
    @Test
    public void testSaveUser_fail_nopassword() {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.empty());
        mockUser.setPassword(null);
        when(userRepositoryImpl.saveUser(any())).thenReturn(new AbstractMap.SimpleEntry<>(null, mockUser));
        createUserRequest.setPassword(null);
        ServiceResponse response = userServiceImpl.saveUser(createUserRequest);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue(response.getMessage().contains("password cannot be null"));
    }

    @Test
    public void testUpdateUser_success() {
        doNothing().when(userRepositoryImpl).updateUser(any(), any());
        ServiceResponse response = userServiceImpl.updateUser(TEST_USER_ID, createUserRequest);
        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatus());
        verify(userRepositoryImpl, times(1)).updateUser(any(), any());
    }
    @Test
    public void testAddFilenameToFileUploadedByUser_success() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        when(userRepositoryImpl.addFilenameToFilesUploaded(any(), any())).thenReturn(mockUser);
        userServiceImpl.addFIlenameToFilesUploadedByUser(MOCK_USERNAME, "test2.pdf");
        verify(userRepositoryImpl, times(1)).addFilenameToFilesUploaded(any(), any());
        verify(userRepositoryImpl, times(1)).getUserByUsername(any());
    }

    @Test
    public void testDeleteFilename_success() throws Exception {
        when(userRepositoryImpl.getUserByUsername(any())).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepositoryImpl).updateUserFilesUploaded(any(), any());
        userServiceImpl.deleteFilenameFromFilesUploaded(MOCK_USERNAME, "test2.pdf");
        verify(userRepositoryImpl, times(1)).getUserByUsername(any());
        verify(userRepositoryImpl, times(1)).updateUserFilesUploaded(any(), any());
    }

}
