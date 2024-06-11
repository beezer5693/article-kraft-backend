package org.brandon.articlekraftbackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String parseAndFormatDateTime(LocalDateTime localDateTime) {
        return LocalDateTime
                .parse(localDateTime.toString())
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss:SS a"));
    }
}
