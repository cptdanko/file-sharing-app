package com.mydaytodo.sfa.asset.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import com.amazonaws.waiters.WaiterParameters;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.AssetUser;
import com.mydaytodo.sfa.asset.model.DocumentMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private DocumentServiceImpl documentService;
    @Autowired
    private UserRepositoryImpl userRepository;

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
    public ServiceResponse uploadFile(MultipartFile file, String userId) throws IOException {

        // perform some validation on how many files does the user already have
        try {
            Optional<AssetUser> optionalUser = userRepository.getUserByUsername(userId);
            AssetUser user = optionalUser.orElseThrow();
            if(user.getAssetsUploaded().size() >= awsConfig.getUploadLimit()) {
                return ServiceResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("You have reached the limit of the no of documents you can upload")
                        .build();

            }
            user.getAssetsUploaded().add(file.getOriginalFilename());

            userRepository.updateUser(user.getUserid(), user);
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
        }


        DocumentMetadataUploadRequest request = new DocumentMetadataUploadRequest();
        request.setName(file.getOriginalFilename());
        // hard coded asset type to DOCUMENT for now
        request.setAssetType("DOCUMENT");
        request.setUserId(userId);
        ServiceResponse metadataUploadResp = documentService.saveDocumentMetadata(request);
        if(metadataUploadResp.getStatus() > 299) {
            return ServiceResponse.builder()
                    .message("Something went wrong, please try again later")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
        String filename = userId + "/" + file.getOriginalFilename();
        log.info(filename);
        return s3Repository.putS3Object(convertMultipartFile(file), filename);
    }

    /**
     * Method to show all files uploaded by user
     * @param userId
     * @return
     */
    public ServiceResponse listFilesInBucket(String userId) {
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
     * Not sure how to work this one out yet
     * @param filename
     */
    public void downloadFile(String filename) {

    }

    /**
     * @param filename
     * @return
     */
    public ServiceResponse deleteFile(String userId, String filename) {
        String fullpath = userId + "/" + filename;
        if(!s3Repository.fileExists(fullpath)) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(filename + " - not found, are you sure you passed the correct filename?")
                    .build();

        }
        return s3Repository.deleteFile(fullpath);
    }
    public ServiceResponse downloadFile(String userId, String filename) throws IOException {
        String fullpath = userId + "/" + filename;
        if(!s3Repository.fileExists(fullpath)) {
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("'" + filename + "' - not found. are you sure you passed the correct filename?")
                    .build();

        }
        return s3Repository.downloadData(fullpath);
    }

    /**
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private File convertMultipartFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;

    }
}
