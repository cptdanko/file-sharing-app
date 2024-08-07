package com.mydaytodo.sfa.asset.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.FileMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.FileType;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import com.mydaytodo.sfa.asset.utilities.StringManipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A storage service to handle file uploads to
 * cloud storage e.g. AWS S3
 */
@Service
@Slf4j
public class StorageServiceImpl {
    @Autowired
    private S3Repository s3Repository;
    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private AWSConfig awsConfig;

    /**
     * @param createBucketRequest
     * @return
     */
    public ServiceResponse createBucket(CreateBucketRequest createBucketRequest) {
        return s3Repository.createBucket(createBucketRequest);
    }

    /**
     * 1. Get the filename along with userid
     * 2. Save all that info in the Document class
     * 3. Save the Document in the DocumentRepositoryImpl
     * 4. Move on to uploading the actual file to S3 via S3Repository
     */
    public ServiceResponse uploadFile(MultipartFile file, String username) throws IOException, Exception {
        // perform some validation on how many files does the user already have
        try {
            ServiceResponse serviceResponse = getFilesUploadedByUser(username);
            @SuppressWarnings("unchecked")
            List<String> files = (List<String>) serviceResponse.getData();
            log.info(String.format("Fetching files by username [ %d ]", files.size()));
            log.info("No of files {}", files.size());
            if (files.size() >= awsConfig.getUploadLimit()) {
                log.info("Max upload limit reached");
                return ServiceResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("You have reached the limit of the no of documents you can upload")
                        .build();

            }
        } catch (SdkClientException e) {
            return ServiceResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
        }

        FileMetadataUploadRequest request = new FileMetadataUploadRequest();
        request.setName(file.getOriginalFilename());
        String extension = StringManipService.getExtension(file.getOriginalFilename());
        log.info("Got the file extension as {}", extension);
        FileType fileType = FileType.getFileTypeFromExtension(extension);
        request.setAssetType(fileType.getType());
        request.setUserId(username);
        log.info(String.format("About to save document metadata [ %s ]", request.toString()));
        ServiceResponse metadataUploadResp = this.fileService.saveDocumentMetadata(request);
        log.info(String.format("Saved the metadata"));
        if (metadataUploadResp.getStatus() > 299) {
            return ServiceResponse.builder()
                    .message("Something went wrong, please try again later")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
        String filename = username + "/" + file.getOriginalFilename();
        log.info(filename);
        File convertedMultiPFile = convertMultipartFile(file);
        ServiceResponse response = s3Repository.putS3Object(convertedMultiPFile, filename);
        userServiceImpl.addFIlenameToFilesUploadedByUser(username, filename);
        convertedMultiPFile.deleteOnExit();
        return response;
    }

    /**
     * Method to show all files uploaded by user
     *
     * @param userId
     * @return
     */
    public ServiceResponse getFilesUploadedByUser(String userId) {
        List<String> objectsSavedByUser = s3Repository.filesByUser(userId);
        log.info("Files found");
        log.info(String.valueOf(objectsSavedByUser.size()));
        return ServiceResponse
                .builder()
                .data(objectsSavedByUser)
                .status(HttpStatus.OK.value())
                .message("")
                .build();
    }

    /**
     * @param filename
     * @return
     */
    @Transactional
    public ServiceResponse deleteFile(String userId, String filename) {
        String fullpath = userId + "/" + filename;
        if (!s3Repository.fileExists(fullpath)) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(filename + " - not found, are you sure you passed the correct filename?")
                    .build();

        }
        log.info("About to delete filename from user array");
        // 1: delete a record of the file from users files uploaded field
        userServiceImpl.deleteFilenameFromFilesUploaded(filename, userId);
        // 2: delete the file from the fileMeta data table by filename and userId
        log.info("About to delete file metadata ");
        fileService.deleteFilesByUser(filename, userId);
        // log.info("The retrieved user is {}", resp.toString());

        return s3Repository.deleteFile(fullpath);
    }

    public ServiceResponse downloadFile(String userId, String filename) throws IOException {
        String fullpath = userId + "/" + filename;
        log.info(String.format("Checking if file [ %s ] exists?", fullpath));
        if (!s3Repository.fileExists(fullpath)) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("'" + filename + "' - not found. are you sure you passed the correct filename?")
                    .build();

        }
        log.info("File exists and now downloading the file");
        return s3Repository.downloadData(fullpath);
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private File convertMultipartFile(MultipartFile multipartFile) throws IOException {
        // File convFile = new File(multipartFile.getOriginalFilename());
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);
        return convFile;
    }
}
