package org.brandon.articlekraftbackend.utils;

import java.util.UUID;

public class UUIDUtils {

    private UUIDUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
