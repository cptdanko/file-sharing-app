package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.CustomCreateBucketRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        InputStream is = file.getInputStream();
        String filename = file.getOriginalFilename();
        log.info("Request to upload file = " +filename);
        ServiceResponse response = storageService.uploadFile(file, userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * @param userId
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<ServiceResponse> listFilesUploadedByUser(@RequestParam("userId") String userId) {
        log.info(String.format("Request to list files for [ %s ]", userId));
        ServiceResponse response = storageService.listFilesInBucket(userId);
        log.info(response.getMessage());
        log.info(response.getStatus()+"");
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    /**
     * @param fileId
     * @return
     */
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<ServiceResponse> deleteFile(@RequestParam("userId") String userId, @PathVariable("fileId") String fileId) {
        log.info(String.format("Request to delete file [ %s ]  by user [ %s ]", fileId, userId));
        ServiceResponse response = storageService.deleteFile(userId, fileId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * TODO: Currently method not working rewrite it
     *
     * @param userId
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> getFileData(@RequestParam("userId") String userId, @PathVariable("fileId") String fileId) throws IOException {
        ServiceResponse response = storageService.downloadFile(userId, fileId);
        //if(response.getStatus().equals(HttpStatus.OK.toString())) {
        ByteArrayResource byteArrayResource = new ByteArrayResource((byte[]) response.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .body(byteArrayResource);
        //}
        //return new ResponseEntity<>(null, HttpStatus.valueOf(response.getStatus()));
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
