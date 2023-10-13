package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.model.User;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;
    private final String BASE_URL = "/api/user";
    JacksonTester<User> userJacksonTester;
    private final String MOCK_USER_ID = "USR_123";
    private User user;
    private MockMvc mockMvc;

    @BeforeEach
    void populate() {
        user = User.builder()
                .name("")
                .assetsUploaded(new String[]{"file1.pdf", "file2.docx"})
                .role("owner")
                .department("finance")
                .dateJoined(Date.from(Instant.now()))
                .lastLogin(Date.from(Instant.now()))
                .password("temp")
                .username("bhuman soni")
                .userid(MOCK_USER_ID)
                .build();
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUser() throws Exception {
        //when(userService.getUser(any())).thenReturn(user);
        mockMvc.perform(get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }
    @Test
    void testCreateUser() throws Exception  {
        // when(userService.createUser(any())).thenReturn(HttpStatus.CREATED.value());
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJacksonTester.write(user).getJson())
        ).andExpect(status().isOk());
    }
    @Test
    void testUpdateUser() throws Exception {
        // when(userService.updateUser(any(), any()).thenReturn(HttpStatus.NO_CONTENT.value()));
        mockMvc.perform(put(BASE_URL + "/" + MOCK_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJacksonTester.write(user).getJson())
        ).andExpect(status().isNoContent());

    }
    @Test
    void testDeleteUser() throws Exception {
        // when(userService.deleteUser(any()).thenReturn(HttpStatus.NO_CONTENT.value()));
        mockMvc.perform(delete(BASE_URL + "/" + MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}