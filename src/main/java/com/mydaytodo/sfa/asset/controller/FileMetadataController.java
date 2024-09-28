package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.db.File;
import com.mydaytodo.sfa.asset.model.FileMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO: add validation for parameters of all
 * incoming requests
 */
@RestController
@RequestMapping("/api/asset")
@Tag(name = "File Metadata Controller")
@Slf4j
public class FileMetadataController {
    @Autowired
    private FileServiceImpl assetService;

    @GetMapping("/detail/{assetId}")
    public ResponseEntity<File> getAssetDetail(@PathVariable("assetId") String id) {
        log.info("Received request to get asset with id "+ id);
        ServiceResponse response = assetService.getDocument(id);
        File doc = (File) response.getData();
        return new ResponseEntity<>(doc, HttpStatus.valueOf(response.getStatus()));
    }
    @PostMapping("/upload")
    public ResponseEntity<HttpStatus> uploadAsset(@RequestBody FileMetadataUploadRequest uploadRequest) {
        log.info("Got the request to upload asset (document)");
        ServiceResponse response = assetService.saveDocumentMetadata(uploadRequest);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @DeleteMapping("/{assetId}")
    public ResponseEntity<HttpStatus> deleteAsset(@PathVariable("assetId")String assetId) {
        log.info("request to delete an asset with id {}", assetId);
        ServiceResponse response = assetService.deleteAsset(assetId);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @PutMapping("/{assetId}")
    public ResponseEntity<HttpStatus> modifyAsset(@PathVariable("assetId")String assetId, @RequestBody FileMetadataUploadRequest uploadRequest) {
        log.info("received request to modify asset with id "+ assetId + " and modify name "+ uploadRequest.getName());
        ServiceResponse response = assetService.updateFileMetadata(assetId, uploadRequest);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @GetMapping("/by/{userId}")
    public ResponseEntity<List<File>> getUserAssets(@PathVariable("userId") String userId) {
        log.info("received request to get assets for user with id {}", userId);
        log.info("");

        List<File> assets = assetService.getUserDocuments(userId);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    /**
     * This method would be used by admin to get
     * all the assets (documents) of a type not
     * limited to a single user.
     * @param type
     * @return
     */
    @GetMapping("/type/{assetType}")
    public ResponseEntity<List<File>> getAssetsOfType(@PathVariable("assetType") String type) {
        log.info("received request to get assets of type "+ type);
        List<File> assets = assetService.getFilesOfType(type);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

}
