package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.model.CreateUserRequest;
import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.FileRepositoryImpl;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import com.mydaytodo.sfa.asset.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class SocialControllerTest {

    @Mock
    private FileServiceImpl fileService;
    @Mock
    private MailService mailService;
    @InjectMocks
    private SocialController socialController;

    JacksonTester<EmailRequest> emailRequestJacksonTester;

    private MockMvc mockMvc;
    private final String BASE_URL = "/api/social";

    @MockBean
    private FileRepositoryImpl fileRepository;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(socialController).build();
    }
    @Test
    void testSendEmail_success() throws Exception {
        // status will be null, hence logic will pass
        when(fileService.validateFileType(any())).thenReturn(ServiceResponse.builder().build());
        when(mailService.sendEmail(any())).thenReturn(TestUtils.noContentResponse);
        mockMvc.perform(post(BASE_URL + "/sendMail")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJacksonTester.write(EmailRequest.builder().build()).getJson()))
                .andExpect(status().isNoContent());
    }
    @Test
    void testSendEmail_fail() throws Exception {
        // status will be null, hence logic will pass
        when(fileService.validateFileType(any())).thenReturn(ServiceResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("File type not supported").build());

        EmailRequest request = EmailRequest.builder().filesToAttach(new String[] {"file1.png"}).build();
        mockMvc.perform(post(BASE_URL + "/sendMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJacksonTester.write(request).getJson()))
                .andExpect(status().isBadRequest());
    }
}
