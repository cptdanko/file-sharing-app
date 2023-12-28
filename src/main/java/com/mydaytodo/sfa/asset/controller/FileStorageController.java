package com.mydaytodo.sfa.asset.controller;

import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.mydaytodo.sfa.asset.model.CustomCreateBucketRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileStorageController {

    @Autowired
    private StorageServiceImpl storageService;

    @PostMapping("/bucket")
    public ResponseEntity<ServiceResponse> createNewBucket(@RequestBody CustomCreateBucketRequest createBucketRequest) {


        return null;
    }
    @GetMapping("/bucket/files/{bucketName}")
    public ResponseEntity<ServiceResponse> listFilesInBucket(@PathVariable("bucketName") String bucketName) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Possible 400 errors could be
     * - 404 user not found
     * - document too large or something
     * @param file
     * @param userId
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload")
    public ResponseEntity<ServiceResponse> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("userId") String userId)
            throws IOException {
        log.info(userId);
        log.info(file.getResource().getDescription());
        InputStream is = file.getInputStream();
        String filename = file.getOriginalFilename();
        log.info(filename);
        return new ResponseEntity<>(storageService.uploadFile(file, userId), HttpStatus.MULTI_STATUS);
    }

    /**
     * @param userId
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<ServiceResponse> listFilesUploadedByUser(@RequestParam("userId") String userId) {
        ServiceResponse response = storageService.listFilesInBucket(userId);
        log.info(response.getMessage());
        log.info(response.getStatus()+"");
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * @param fileId
     * @return
     */
    @DeleteMapping("/{fileId}/delete")
    public ResponseEntity<ServiceResponse> deleteFile(@RequestParam("userId") String userId, @PathVariable("fileId") String fileId) {
        ServiceResponse response = storageService.deleteFile(userId, fileId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    @GetMapping("/{fileId}/download")
    public ResponseEntity<ServiceResponse> getFileData(@RequestParam("userId") String userId, @PathVariable("fileId") String fileId) throws IOException {
        ServiceResponse response = storageService.downloadFile(userId, fileId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * @param fileId
     * @return
     */
    @RequestMapping("/updateFile")
    public ResponseEntity<ServiceResponse> replaceUploadedFile(@PathVariable("fileId") String fileId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
