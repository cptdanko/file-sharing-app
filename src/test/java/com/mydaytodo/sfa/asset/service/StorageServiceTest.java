package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import com.mydaytodo.sfa.asset.utilities.StringManipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {

    @InjectMocks
    @Spy
    private StorageServiceImpl storageService;

    @Mock
    private S3Repository s3Repository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private AWSConfig awsConfig;
    private MultipartFile mockFile;


    private static final String TST_USR_ID = "USR_123";
    private static final String TST_FILE_NAME= "test.pdf";

    @Test
    public void downloadFileTest_success() throws IOException {
        when(s3Repository.fileExists(any())).thenReturn(true);
        when(s3Repository.downloadData(any())).thenReturn(TestUtils.successResponse);
        ServiceResponse response = storageService.downloadFile(TST_USR_ID, TST_FILE_NAME);
        verify(s3Repository, times(1)).fileExists(any());
        verify(s3Repository, times(1)).downloadData(any());
    }
    @Test
    public void downloadFileTest_fail() throws IOException {
        when(s3Repository.fileExists(any())).thenReturn(false);
        ServiceResponse response = storageService.downloadFile(TST_USR_ID, TST_FILE_NAME);
        assertEquals(response.getStatus().intValue(), HttpStatus.NOT_FOUND.value());
        verify(s3Repository, times(1)).fileExists(any());
        verify(s3Repository, times(0)).downloadData(any());
    }

    @Test
    public void deleteFileTest_success() {
        when(s3Repository.fileExists(any())).thenReturn(true);
        doNothing().when(userService).deleteFilenameFromFilesUploaded(any(), any());
        doNothing().when(fileService).deleteFilesByUser(any(), any());
        when(s3Repository.deleteFile(any())).thenReturn(TestUtils.noContentResponse);
        ServiceResponse response = storageService.deleteFile(TST_USR_ID, TST_FILE_NAME);
        assertEquals(response.getStatus().intValue(), HttpStatus.NO_CONTENT.value());
        verify(userService, times(1)).deleteFilenameFromFilesUploaded(any(), any());
        verify(fileService, times(1)).deleteFilesByUser(any(), any());
    }
    @Test
    public void deleteFileTest_fail() {
        when(s3Repository.fileExists(any())).thenReturn(false);
        ServiceResponse response = storageService.deleteFile(TST_USR_ID, TST_FILE_NAME);
        assertEquals(response.getStatus().intValue(), HttpStatus.NOT_FOUND.value());
        verify(userService, times(0)).deleteFilenameFromFilesUploaded(any(), any());
        verify(fileService, times(0)).deleteFilesByUser(any(), any());
    }

    @Test
    public void getFilesUploadedByUserTest_fail() {
        when(s3Repository.filesByUser(any())).thenReturn(new ArrayList<>());
        ServiceResponse response = storageService.getFilesUploadedByUser(TST_USR_ID);
        assertEquals(response.getStatus().intValue(), HttpStatus.OK.value());
        verify(s3Repository, times(1)).filesByUser(any());
    }
    @Test
    public void uploadFileTest_success() throws Exception {
        List<String> tempFileList = new ArrayList<>(){{ add("file1.pdf"); add("file2.pdf"); add("file3.pdf");}};
        when(s3Repository.filesByUser(any())).thenReturn(tempFileList);
        when(awsConfig.getUploadLimit()).thenReturn(20);
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("temp.pdf");

        try(MockedStatic<StringManipService> mocked = mockStatic(StringManipService.class)) {
            mocked.when(() -> StringManipService.getExtension(any())).thenReturn("pdf");
            doReturn(TestUtils.createdResponse).when(storageService).saveFileMetadata(any(), any());
            doReturn(File.createTempFile("sdf", ".pdf")).when(storageService).convertMultipartFile(any());
            when(s3Repository.putS3Object(any(), any())).thenReturn(TestUtils.createdResponse);
            ServiceResponse response = storageService.uploadFile(mockFile, TST_USR_ID);
            assertEquals(response.getStatus().intValue(), HttpStatus.CREATED.value());
            verify(s3Repository, times(1)).filesByUser(any());
            verify(s3Repository, times(1)).putS3Object(any(), any());
            verify(awsConfig, times(1)).getUploadLimit();
            verify(storageService, times(1)).convertMultipartFile(any());
        }
    }
    @Test
    public void uploadFileTest_fail_limitreached() throws Exception {
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("temp.pdf");
        List<String> tempFileList = new ArrayList<>(){{ add("file1.pdf"); add("file2.pdf"); add("file3.pdf");}};
        when(s3Repository.filesByUser(any())).thenReturn(tempFileList);
        when(awsConfig.getUploadLimit()).thenReturn(2);
        ServiceResponse response = storageService.uploadFile(mockFile, TST_USR_ID);
        assertEquals(response.getStatus().intValue(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void uploadFileTest_file_savemetaData_issue() throws Exception  {
        List<String> tempFileList = new ArrayList<>(){{ add("file1.pdf"); add("file2.pdf"); add("file3.pdf");}};
        when(s3Repository.filesByUser(any())).thenReturn(tempFileList);
        when(awsConfig.getUploadLimit()).thenReturn(20);
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("temp.pdf");

        try(MockedStatic<StringManipService> mocked = mockStatic(StringManipService.class)) {
            mocked.when(() -> StringManipService.getExtension(any())).thenReturn("pdf");
            doReturn(TestUtils.forbiddenResponse).when(storageService).saveFileMetadata(any(), any());
            ServiceResponse response = storageService.uploadFile(mockFile, TST_USR_ID);
            assertEquals(response.getStatus().intValue(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            verify(s3Repository, times(1)).filesByUser(any());
            verify(awsConfig, times(1)).getUploadLimit();

            verify(storageService, times(0)).convertMultipartFile(any());
            verify(s3Repository, times(0)).putS3Object(any(), any());
        }
    }
}
