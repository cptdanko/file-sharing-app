package com.mydaytodo.sfa.asset.model;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@ToString
@Slf4j
public enum FileType {
    DOCUMENT("document", new String[]{"doc", "docx"}, "MS Word"),
    PDF("pdf", new String[]{"pdf"}, "PDF"),
    MD("md", new String[]{"md"}, "Markdown"),
    TXT("text", new String[]{"txt"}, "Text"),
    JSON("json", new String[]{"json"}, "JSON");

    private final String type;
    private final String name;
    private final String[] extensions;

    FileType(String type, String[] extension, String name) {
        this.type = type;
        this.extensions = extension;
        this.name = name;
    }
    public static FileType fromTypeStr(String name) {
        for(FileType type: FileType.values()) {
            if(type.getType().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unable to create enum from "+ name);
    }
    public static FileType getFileTypeFromExtension(String extension) {
        log.info("About to get extension name from {}", extension);
        for(FileType type: FileType.values()) {
            if(Arrays.stream(type.getExtensions())
                    .anyMatch(e -> e.equalsIgnoreCase(extension))) {
                return type;
            }
        }
        log.info("Unable to retrieve file type from extension {}",extension);
        return null;
    }
    public static List<FileType> supportedFileTypes() {
        return Arrays.asList(FileType.values());
    }
}
