package org.brandon.articlekraftbackend.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {

    private DateTimeHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String parseAndFormatDateTime(LocalDateTime localDateTime) {
        return LocalDateTime
                .parse(localDateTime.toString())
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss:SS a"));
    }
}
