package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.File;
import com.mydaytodo.sfa.asset.model.FileMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.FileRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepositoryImpl fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    public static FileMetadataUploadRequest fileMetadataUploadRequest;

    @BeforeAll
    public static void setup() {
        fileMetadataUploadRequest = FileMetadataUploadRequest.builder()
                .path("/file/temp")
                .name("Topsecret.pdf")
                .assetType("document")
                .userId(KeyStart.USER_KEY + "123123")
                .build();
    }

    @Test
    public void saveDocumentMetadataTest_success() {
        when(fileRepository.saveFileMetadata(any())).thenReturn(TestUtils.createdResponse.getStatus());
        ServiceResponse response = fileService.saveDocumentMetadata(FileServiceTest.fileMetadataUploadRequest);

        assertEquals(response.getStatus(), HttpStatus.CREATED.value());
    }

    @Test
    public void getDocumentTest_success() {
        when(fileRepository.getDocument(any())).thenReturn(new File());
        ServiceResponse response = fileService.getDocument(any());
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }
    @Test
    public void getDocumentTest_fail() {
        when(fileRepository.getDocument(any())).thenReturn(null);
        ServiceResponse response = fileService.getDocument(any());
        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());
    }
    @Test
    public void validateFileTypeTest_success() {
        String [] filenames = {"file1.pdf", "file2.doc", "file3.txt"};
        ServiceResponse response = fileService.validateFileType(filenames);
        Assertions.assertNull(response.getStatus());
    }
    @Test
    public void validateFileTypeTest_fail() {
        String [] filenames = {"file1.png", "file2.doc", "file3.txt"};
        ServiceResponse response = fileService.validateFileType(filenames);
        assertEquals(response.getStatus().intValue(), HttpStatus.BAD_REQUEST.value());
    }
}
