package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.Document;
import com.mydaytodo.sfa.asset.model.DocumentMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.DocumentServiceImpl;
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
@Slf4j
public class AssetController {
    @Autowired
    private DocumentServiceImpl assetService;

    @GetMapping("/detail/{assetId}")
    public ResponseEntity<Document> getAssetDetail(@PathVariable("assetId") String id) {
        log.info("Received request to get asset with id "+ id);
        ServiceResponse response = assetService.getDocument(id);
        Document doc = (Document) response.getData() ;
        return new ResponseEntity<>(doc, HttpStatus.valueOf(response.getStatus()));
    }
    @PostMapping("/upload")
    public ResponseEntity<HttpStatus> uploadAsset(@RequestBody DocumentMetadataUploadRequest uploadRequest) {
        log.info("Got the request to upload asset (document)");
        ServiceResponse response = assetService.saveDocumentMetadata(uploadRequest);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @DeleteMapping("/{assetId}")
    public ResponseEntity<HttpStatus> deleteAsset(@PathVariable("assetId")String assetId) {
        log.info("request to delete an asset with id "+ assetId);
        ServiceResponse response = assetService.deleteAsset(assetId);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @PutMapping("/{assetId}")
    public ResponseEntity<HttpStatus> modifyAsset(@PathVariable("assetId")String assetId, @RequestBody DocumentMetadataUploadRequest uploadRequest) {
        log.info("received request to modify asset with id "+ assetId + " and modify name "+ uploadRequest.getName());
        ServiceResponse response = assetService.updateDocumentMetadata(assetId, uploadRequest);
        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }
    @GetMapping("/by/{userId}")
    public ResponseEntity<List<Document>> getUserAssets(@PathVariable("userId") String userId) {
        log.info("received request to get assets for user with id "+ userId);
        log.info("");

        List<Document> assets = assetService.getUserDouments(userId);
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
    public ResponseEntity<List<Document>> getAssetsOfType(@PathVariable("assetType") String type) {
        log.info("received request to get assets of type "+ type);
        List<Document> assets = assetService.getDocumentsOfType(type);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

}
