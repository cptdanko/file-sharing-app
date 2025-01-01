package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import com.mydaytodo.sfa.asset.service.StorageServiceImpl;
import com.mydaytodo.sfa.asset.service.UserFileServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@Tag(name = "File Storage Controller")
@Slf4j
public class FileStorageController {

    @Autowired
    private StorageServiceImpl storageService;

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private UserFileServiceImpl userFileService;

    /**
     * Possible 400 errors could be
     * - 404 user not found
     * - document too large or something
     *
     * @param file
     * @param username
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public ResponseEntity<ServiceResponse> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("username") String username)
            throws Exception {
        log.info("Original filename to upload {}", file.getOriginalFilename());
        Validator.validateUsernameAndToken(username);
        String[] filenames = new String[]{file.getOriginalFilename()};
        if(fileService.validateFileType(filenames).getStatus() != null) {
            ServiceResponse response = fileService.validateFileType(filenames);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        }
        ServiceResponse response = storageService.uploadFile(file, username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * TODO: move this method to the UserController
     * @param userId
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<ServiceResponse> listFilesUploadedByUser(@RequestParam("userId") String userId) {
        log.info("Request to list files for [ {} ]", userId);
        Validator.validateUsernameAndToken(userId);
        ServiceResponse response = storageService.getFilesUploadedByUser(userId);
        log.info(response.getStatus().toString());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    @GetMapping("/by")
    public ResponseEntity<ServiceResponse> listFilesWithSchedulesByUser(@RequestParam("username") String username) {
        log.info("in the method to get files uploaded by user with schedules");
        Validator.validateUsernameAndToken(username);
        ServiceResponse response = userFileService.getUserFileSchedules(username);
        log.info("Received the response from UserFileService of status, {}", response.getStatus());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /**
     * @param fileId
     * @return
     */
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<ServiceResponse> deleteFile(@RequestParam("userId") String userId,
                                                      @PathVariable("fileId") String fileId) {
        log.info("Request to delete file [ {} ]  by user [ {} ]", fileId, userId);
        Validator.validateUsernameAndToken(userId);
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
    public ResponseEntity<Resource> getFileData(@RequestParam("userId") String userId,
                                                @PathVariable("fileId") String fileId) throws IOException {
        log.info("Downloading files for {} with name {}", userId, fileId);
        ServiceResponse response = storageService.downloadFile(userId, fileId);
        ByteArrayResource byteArrayResource = new ByteArrayResource((byte[]) response.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .contentLength(byteArrayResource.contentLength())
                .body(byteArrayResource);
    }

    /**
     * TODO: have to implement replacing the uploaded file
     * @param fileId
     * @return
     */
    @PutMapping("/updateFile")
    public ResponseEntity<ServiceResponse> replaceUploadedFile(@PathVariable("fileId") String fileId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
