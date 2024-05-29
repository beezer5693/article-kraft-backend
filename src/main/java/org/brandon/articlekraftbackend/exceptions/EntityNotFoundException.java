package org.brandon.articlekraftbackend.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final String identifier;

    public EntityNotFoundException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }
}
