package com.mydaytodo.sfa.asset.utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringManipService {

    // valid extensions for attaching files
    public static String[] VALID_EXT =
            new String[]{"pdf", "doc", "docx", "txt", "md", "json"};

    public static String getExtension(String filePath) {
        log.info("About to extract extension from {}", filePath);
        if (filePath.lastIndexOf('.') + 1 == filePath.length()) {
            String msg = String.format("Error in file path %s", filePath);
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return filePath.substring(filePath.lastIndexOf('.') + 1);
    }
}
