package com.mydaytodo.sfa.asset.model;

public enum DocumentType {
    DOCUMENT("document"),
    IMAGE("image"),
    PDF("pdf");

    private DocumentType documentType;

    DocumentType(String type) {
        documentType = DocumentType.valueOf(type);
    }

}
