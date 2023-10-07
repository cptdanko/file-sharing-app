package com.mydaytodo.sfa.asset.model;

public enum AssetType {
    DOCUMENT("document"),
    IMAGE("image"),
    PDF("pdf");

    private AssetType assetType;

    AssetType(String type) {
        assetType = AssetType.valueOf(type);
    }

}
