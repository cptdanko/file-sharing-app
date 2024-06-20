package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class S3Repository {
    @Autowired
    private AWSConfig awsConfig;

    public boolean fileExists(String filename) {
        log.info(String.format("Checking if [ %s ] file exists", filename));
        boolean fileExists = awsConfig.s3Client().doesObjectExist(awsConfig.getS3UploadBucketName(), filename);
        log.info(String.format("File exists? -> [  %s ]", fileExists));
        return fileExists;
    }

    public ServiceResponse putS3Object(File file,
                                       String assetKey) throws IOException {
        log.info("About to upload a file to S3");

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(awsConfig.getS3UploadBucketName()
                    , assetKey, file);

            PutObjectResult result = awsConfig.s3Client().putObject(putObjectRequest);
            log.info("Finished uploading..........");
            log.info(awsConfig.getS3UploadBucketName());
            return ServiceResponse.builder()
                    .data(null)
                    .message(result.getMetadata().toString())
                    .status(HttpStatus.CREATED.value())
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .data(null)
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    /**
     * Not actively used at the moment
     *
     * @param createBucketRequest
     * @return
     * @throws SdkClientException
     * @throws AmazonServiceException
     */
    public ServiceResponse createBucket(CreateBucketRequest createBucketRequest)
            throws SdkClientException, AmazonServiceException {
        // awsConfig.s3Client().createBucket()
        AmazonS3Client client = new AmazonS3Client();
        try {
            AmazonS3Waiters s3waiters = client.waiters();

            log.info("About to send request for name " + createBucketRequest.getBucketName() + " and region " + createBucketRequest.getRegion());
            Bucket bucket = client.createBucket(createBucketRequest.getBucketName(), createBucketRequest.getRegion());
            HeadBucketRequest requestWait = new HeadBucketRequest(createBucketRequest.getBucketName());
            log.info("Successfully created the bucket");
            return ServiceResponse.builder()
                    .data(bucket)
                    .status(HttpStatus.CREATED.value())
                    .message("")
                    .build();
        } catch (AmazonServiceException amazonServiceException) {

            log.error(amazonServiceException.getLocalizedMessage());
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(amazonServiceException.getLocalizedMessage())
                    .build();

        } catch (SdkClientException sdkClientException) {
            log.error(sdkClientException.getLocalizedMessage());
            return ServiceResponse.builder()
                    .data(null)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(sdkClientException.getLocalizedMessage() + " error code \n" + sdkClientException.getMessage())
                    .build();
        }
    }

    /**
     * Return a list of objects stored in user's
     * s3 folder (object)
     *
     * @param userId
     * @return
     */
    public List<String> filesByUser(String userId) throws SdkClientException, AmazonServiceException {
        List<String> filenames = new ArrayList<>();
        String bucketName = awsConfig.getS3UploadBucketName();
        ObjectListing objects = awsConfig.s3Client().listObjects(awsConfig.getS3UploadBucketName(), userId + "/");
        List<S3ObjectSummary> summaries = objects.getObjectSummaries();
        log.info(String.format("Printing files by user -> [ %s ]", userId));
        log.info("-----------------------------------");
        for (S3ObjectSummary summary : summaries) {
            String filename = summary.getKey().substring(summary.getKey().indexOf("/") + 1);
            log.info(String.format("file [ %s ] uploaded of size [ %d ] bytes", filename, summary.getSize()));
            filenames.add(filename);
        }
        log.info("-----------------------------------");
        return filenames;
    }

    public ServiceResponse deleteFile(String filename) throws SdkClientException, AmazonServiceException {
        DeleteObjectRequest request = new DeleteObjectRequest(awsConfig.getS3UploadBucketName(), filename);
        try {
            awsConfig.s3Client().deleteObject(request);
            return ServiceResponse.builder()
                    .message("Success")
                    .status(HttpStatus.NO_CONTENT.value())
                    .build();
        } catch (AmazonServiceException sce) {
            return ServiceResponse.builder()
                    .message(sce.getMessage())
                    .status(sce.getStatusCode())
                    .build();
        }
    }

    /**
     * @param filename
     * @return
     */
    public ServiceResponse downloadData(String filename) throws IOException {
        S3Object object = awsConfig.s3Client().getObject(awsConfig.getS3UploadBucketName(), filename);
        try (S3ObjectInputStream s3is = object.getObjectContent()) {
            log.info("Got the S3ObjectInputStream.....");
            log.info(s3is.toString());
            log.info("{}", s3is.available());
            String modFilename = filename.substring(filename.indexOf("/") + 1);
            log.info("Mod filename [ {} ]", modFilename);
            return ServiceResponse.builder()
                    .status(HttpStatus.OK.value())
                    .data(s3is.readAllBytes())
                    .message("")
                    .build();
        } catch (IOException io) {
            log.error(io.getMessage());
            return ServiceResponse.builder()
                    .data(null)
                    .message(io.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    public byte[] getDataForFile(String filename) throws IOException {
        S3Object object = awsConfig.s3Client().getObject(awsConfig.getS3UploadBucketName(), filename);
        try (S3ObjectInputStream s3is = object.getObjectContent()) {
            log.info(s3is.toString());
            log.info("s3 inputStream availability {}", s3is.available());
            String modFilename = filename.substring(filename.indexOf("/") + 1);
            log.info("Modified filename [ {} ]", modFilename);
            return s3is.readAllBytes();
        } catch (IOException io) {
            log.error(io.getMessage());
            return null;
        }
    }

}
