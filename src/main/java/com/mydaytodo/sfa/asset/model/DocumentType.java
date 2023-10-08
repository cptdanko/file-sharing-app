package com.mydaytodo.sfa.asset.model;

import lombok.Getter;

@Getter
public enum DocumentType {
    DOCUMENT("document"),
    IMAGE("image"),
    PDF("pdf");

    private final String type;


    DocumentType(String type) {
        this.type = type;
    }
    public static DocumentType fromTypeStr(String name) {
        for(DocumentType type: DocumentType.values()) {
            if(type.getType().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unable to create enum from "+ name);
    }

}
