package org.brandon.articlekraftbackend.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS("access_token"),
    REFRESH("refresh_token");

    private final String value;
}
