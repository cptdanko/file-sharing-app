package com.mydaytodo.sfa.asset.model;

import lombok.Getter;

@Getter
public enum FileType {
    DOCUMENT("document"),
    IMAGE("image"),
    PDF("pdf");

    private final String type;


    FileType(String type) {
        this.type = type;
    }
    public static FileType fromTypeStr(String name) {
        for(FileType type: FileType.values()) {
            if(type.getType().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unable to create enum from "+ name);
    }

}
