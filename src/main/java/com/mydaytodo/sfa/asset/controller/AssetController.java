package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.Asset;
import com.mydaytodo.sfa.asset.model.AssetType;
import com.mydaytodo.sfa.asset.model.AssetUploadRequest;
import com.mydaytodo.sfa.asset.service.AssetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/asset")
/**
 * TODO: imlement all the methods
 */
public class AssetController {
    @Autowired
    private AssetServiceImpl assetService;

    @GetMapping("/detail/{assetId}")
    public ResponseEntity<AssetType> getAssetDetail(@RequestParam("assetId") String id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/upload")
    public ResponseEntity<HttpStatus> uploadAsset(@RequestBody AssetUploadRequest uploadRequest) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @DeleteMapping("/{assetId}")
    public ResponseEntity<HttpStatus> deleteAsset(@RequestParam("assetId")String assetId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{assetId}")
    public ResponseEntity<HttpStatus> modifyAsset(@RequestParam("assetId")String assetId, @RequestBody AssetUploadRequest uploadRequest) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/by/{userId}")
    public ResponseEntity<List<Asset>> getUserAssets(@RequestParam("userId") String userId) {
        List<Asset> assets = new ArrayList<>();
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
    @GetMapping("/type/{assetType}")
    public ResponseEntity<List<Asset>> getAssetsOfType(@RequestParam("assetType") String type) {
        List<Asset> assets = new ArrayList<>();
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

}
