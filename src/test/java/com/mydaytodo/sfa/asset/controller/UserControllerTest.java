package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.db.FileUser;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ExtendWith(MockitoExtension.class)
// @MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;
    private final String BASE_URL = "/api/user";
    JacksonTester<FileUser> userJacksonTester;
    JacksonTester<CreateUserRequest> createUserRequestJacksonTester;
    private final String MOCK_USER_ID = "USR_123";
    private FileUser assetUser;
    private MockMvc mockMvc;
    private final CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .name("Cpt.Danko")
            .filesUploaded(new ArrayList<>())
            .build();

    @BeforeEach
    void populate() {
        assetUser = FileUser.builder()
                .name("")
                .filesUploaded(Arrays.asList("file1.pdf", "file2.docx"))
                .dateJoined(Date.from(Instant.now()))
                .lastLogin(Date.from(Instant.now()))
                .password("temp")
                .username("bhuman soni")
                .userid(MOCK_USER_ID)
                .isSocialLoginGoogle(false)
                .build();

        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    void testGetUser() throws Exception {
        String url = String.format("%s/%s", BASE_URL, MOCK_USER_ID);
        when(userService.getUser(any())).thenReturn(TestUtils.successResponse);
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
    @Test
    void testCreateUser() throws Exception  {
        ServiceResponse createSuccess = ServiceResponse.builder().status(201).build();
        when(userService.saveUser(any())).thenReturn(createSuccess);
        String url = String.format("%s/create", BASE_URL);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserRequestJacksonTester.write(createUserRequest).getJson())
        ).andExpect(status().isCreated());
    }
    @Test
    void testUpdateUser() throws Exception {
        ServiceResponse updateResponse = ServiceResponse.builder().status(204).build();
        when(userService.updateUser(any(), any())).thenReturn(updateResponse);
        mockMvc.perform(put(BASE_URL + "/" + MOCK_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserRequestJacksonTester.write(createUserRequest).getJson())
        ).andExpect(status().isNoContent());
    }
    @Test
    void testFilesVisibleToUser() throws Exception {
        try(MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            when(userService.getFilesAccessibleToUser(any())).thenReturn(TestUtils.successResponse);
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            mockMvc.perform(get(BASE_URL + "/files")
                    .header("Authorization" ,"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZWxuYWNhQGdtYWlsLmNvbSIsImlhdCI6MTcyNDM4MjcwOCwiZXhwIjoxNzI0NDIxNjAwfQ.MKUQ5uXAd2Iy_kXKcw-n-ezyuam7mjxwtsTFZyFX0Zc")
                    .param("username", "kelnaca@gmail.com")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        }
    }
    @Test
    void testGetUserByUsername() throws Exception {
        when(userService.getByUsername(any())).thenReturn(TestUtils.successResponse);
        try(MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            mockMvc.perform(get(BASE_URL + "/username")
                    .param("username", "kelnaca@gmail. com")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        }
    }
}