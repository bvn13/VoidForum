package ru.bvn13.voidforum.models.support;

import java.util.Hashtable;

public class HttpContentTypeSerializer {

    private static final Hashtable<String, String> variants = new Hashtable<String, String>();

    private static final String defaultVariant = "application/octet-stream";

    static {
        variants.put("aac", "audio/aac");
        variants.put("abw", "application/x-abiword");
        variants.put("", "");
        variants.put("", "");
        variants.put("", "");
        variants.put("", "");
        variants.put("", "");
        variants.put("", "");
    }

    public static String getContentType(String fileName) {

        if (fileName.isEmpty()) {
            return defaultVariant;
        }

        String fileArray[]=fileName.split("\\.");

        String extension = fileArray[fileArray.length-1];

        if (variants.containsKey(extension)) {
            return variants.get(extension);
        }

        return defaultVariant;
    }


}
