package org.brandon.articlekraftbackend.utils;

import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
