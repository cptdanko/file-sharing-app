package com.mydaytodo.sfa.asset.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.model.db.File;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class FileMetadataControllerTest {

    private MockMvc  mockMvc;
    @Mock
    private FileServiceImpl documentService;

    @InjectMocks
    private FileMetadataController assetController;

    private File file = new File();
    private final ServiceResponse serviceResponse = new ServiceResponse();
    JacksonTester<File> documentJacksonTester;
    private final String BASE_URL = "/api/asset/";
    private final String MOCK_ASSET_ID = "AST_1234";

    @BeforeEach
    void populate() {

        file = File.builder()
                .id("DOC_123")
                .assetType("document")
                .keyStorePath("/opt/document")
                .name("test")
                .userId("USR_123")
                .build();
        serviceResponse.setData(file);
        serviceResponse.setStatus(HttpStatus.OK.value());
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(assetController)
                .build();
    }

    @Test
    void testGetAssetDetail() throws Exception {
        when(documentService.getDocument(any())).thenReturn(serviceResponse);
        mockMvc.perform(get("/api/asset/detail/AST_123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(documentJacksonTester.write(file).getJson())
        ).andExpect(status().isOk());
    }
    @Test
    void testUploadAsset() throws Exception {

        when(documentService.saveDocumentMetadata(any())).thenReturn(ServiceResponse.builder()
                .status(HttpStatus.CREATED.value()).build());
        mockMvc.perform(post("/api/asset/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentJacksonTester.write(file).getJson()))
                .andExpect(status().isCreated());

    }
    @Test
    void testDeleteAsset() throws Exception {
        when(documentService.deleteAsset(any())).thenReturn(ServiceResponse
                .builder()
                .status(HttpStatus.NO_CONTENT.value()).build());
        mockMvc.perform(delete(BASE_URL+MOCK_ASSET_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void testModifyAsset() throws Exception {
        when(documentService.updateFileMetadata(any(), any())).thenReturn(ServiceResponse.builder()
                .data(null).status(HttpStatus.NO_CONTENT.value()).build());
        mockMvc.perform(put(BASE_URL+"/"+MOCK_ASSET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(documentJacksonTester.write(file).getJson())
        ).andExpect(status().isNoContent());
    }
    @Test
    void testGetUserAssets() throws Exception {
        when(documentService.getUserDocuments(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(get(BASE_URL + "/by/USR_123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
    @Test
    void testGetAssetOfType() throws Exception {
        when(documentService.getFilesOfType(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(get(BASE_URL + "/type/document")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}