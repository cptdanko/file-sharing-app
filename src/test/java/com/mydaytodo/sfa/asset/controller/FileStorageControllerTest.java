package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import com.mydaytodo.sfa.asset.service.StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class FileStorageControllerTest {
    @Mock
    private StorageServiceImpl storageService;

    @Mock
    private FileServiceImpl fileService;

    @InjectMocks
    private FileStorageController fileStorageController;
    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api/file";

    @BeforeEach
    void setup() {
         JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(fileStorageController).build();
    }
    @Test
    void testHandleFileUpload() throws Exception {
        when(storageService.uploadFile(any(), any())).thenReturn(TestUtils.successResponse);
        when(fileService.validateFileType(any())).thenReturn(ServiceResponse.builder().build());
        MockMultipartFile file = new MockMultipartFile("file",
                "test.pdf",
                MediaType.TEXT_PLAIN_VALUE,
                "Pdf test".getBytes());;
        try(MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            String url = BASE_URL + "/upload";
            System.out.println(url);
            mockMvc.perform(multipart(url)
                        .file(file)
                        .param("username", "kelnaca@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk());
        }
    }
    @Test
    void testListFilesUploadedByUser() throws Exception {
        when(storageService.getFilesUploadedByUser(any())).thenReturn(TestUtils.successResponse);
        try (MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            mockMvc.perform(get(BASE_URL + "/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", "kelnaca@gmail.com"))
                    .andExpect(status().isOk());
        }
    }
    @Test
    void testDeleteByUser() throws Exception {
        when(storageService.deleteFile(any(), any())).thenReturn(TestUtils.noContentResponse);
        try (MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            mockMvc.perform(delete(BASE_URL + "/delete/FILE_123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("userId", "kelnaca@gmail.com"))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    void testGetFileData() throws Exception {
        when(storageService.downloadFile(any(), any()))
                .thenReturn(ServiceResponse
                .builder().data("new by arr".getBytes())
                .status(HttpStatus.OK.value()).build());
        //try (MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            //mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
        mockMvc.perform(get(BASE_URL + "/FILE_123/download")
            .contentType(MediaType.APPLICATION_JSON)
            .param("userId", "kelnaca@gmail.com"))
        .andExpect(status().isOk());
        //}
    }
}
