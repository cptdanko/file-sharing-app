package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.Document;
import com.mydaytodo.sfa.asset.model.DocumentUploadRequest;
import com.mydaytodo.sfa.asset.service.DocumentServiceImpl;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/asset")
@Slf4j
public class AssetController {
    @Autowired
    private DocumentServiceImpl assetService;

    @GetMapping("/detail/{assetId}")
    public ResponseEntity<HttpStatus> getAssetDetail(@PathVariable("assetId") String id) {
        log.info("Received request to get asset with id "+ id);
        return assetService.getDocument(id);
    }
    @PostMapping("/upload")
    public ResponseEntity<HttpStatus> uploadAsset(@RequestBody DocumentUploadRequest uploadRequest) {
        return new ResponseEntity<>(assetService.saveDocumentMetadata(uploadRequest));
    }
    @DeleteMapping("/{assetId}")
    public ResponseEntity<HttpStatus> deleteAsset(@PathVariable("assetId")String assetId) {
        return new ResponseEntity<>(assetService.deleteAsset(assetId));
    }
    @PutMapping("/{assetId}")
    public ResponseEntity<HttpStatus> modifyAsset(@PathVariable("assetId")String assetId, @RequestBody DocumentUploadRequest uploadRequest) {
        return new ResponseEntity<>(assetService.updateDocumentMetadata(assetId, uploadRequest));
    }
    @GetMapping("/by/{userId}")
    public ResponseEntity<List<Document>> getUserAssets(@PathVariable("userId") String userId) {
        List<Document> assets = new ArrayList<>();
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
    @GetMapping("/type/{assetType}")
    public ResponseEntity<List<Document>> getAssetsOfType(@PathVariable("assetType") String type) {
        List<Document> assets = new ArrayList<>();
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

}
