package com.mydaytodo.sfa.asset.utilities;

public class StringManipService {

    // valid extensions for attaching files
    public static String[] VALID_EXT =
            new String[]{"pdf", "doc", "docx", "txt", "md", "json"};

    public static String getExtension(String filePath) {
        if (filePath.lastIndexOf('.') + 1 == filePath.length()) {
            String msg = String.format("Error in file path %s", filePath);
            throw new RuntimeException(msg);
        }
        return filePath.substring(filePath.lastIndexOf('.') + 1);
    }
}
