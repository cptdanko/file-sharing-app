package com.mydaytodo.sfa.asset.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import com.amazonaws.waiters.WaiterParameters;
import com.mydaytodo.sfa.asset.config.AWSConfig;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class S3Repository {
    public static String BUCKET_NAME = "mdtupload";
    @Autowired
    private AWSConfig awsConfig;

    public boolean fileExists(String filename){
        boolean doesItExist = awsConfig.s3Client().doesObjectExist(BUCKET_NAME, filename);
        return doesItExist;
    }

    public ServiceResponse putS3Object(File file,
                            String assetKey) throws IOException {
        log.info("About to upload a file to S3");

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(awsConfig.getS3UploadBucketName()
                    , assetKey, file);

            PutObjectResult result = awsConfig.s3Client().putObject(putObjectRequest);
            log.info("Finished uploading..........");
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

            log.info("About to send request for name "+ createBucketRequest.getBucketName() + " and region "+ createBucketRequest.getRegion());
            Bucket bucket = client.createBucket(createBucketRequest.getBucketName(), createBucketRequest.getRegion());
            HeadBucketRequest requestWait = new HeadBucketRequest(createBucketRequest.getBucketName());
            s3waiters.bucketExists().run(new WaiterParameters<>());
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
                    .message(sdkClientException.getLocalizedMessage() +" error code \n"+ sdkClientException.getMessage())
                    .build();
        }
    }

    /**
     * Return a list of objects stored in user's
     * s3 folder (object)
     * @param userId
     * @return
     */
    public List<String> filesByUser(String userId) throws SdkClientException, AmazonServiceException {
        List<String> filenames = new ArrayList<>();
        ObjectListing objects = awsConfig.s3Client().listObjects(BUCKET_NAME, userId + "/");
        List<S3ObjectSummary> summaries = objects.getObjectSummaries();
        log.info("About to print out object summaries");
        for(S3ObjectSummary summary: summaries) {
            log.info(summary.getKey());
            filenames.add(summary.getKey().substring(summary.getKey().indexOf("/") + 1));
        }
        return filenames;
    }

    public ServiceResponse deleteFile(String filename) throws SdkClientException, AmazonServiceException {
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET_NAME, filename);
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
        S3Object object = awsConfig.s3Client().getObject(BUCKET_NAME, filename);
        try (S3ObjectInputStream s3is = object.getObjectContent()) {
            log.info("Got the S3ObjectInputStream.....");
            try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                log.info("In the file output stream");
                byte [] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fileOutputStream.write(read_buf, 0, read_len);
                }
                return ServiceResponse.builder()
                        .status(HttpStatus.OK.value())
                        .data(fileOutputStream)
                        .message("")
                        .build();
            }
        } catch (IOException io) {
            return ServiceResponse.builder()
                    .data(null)
                    .message(io.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }
}
