package org.brandon.articlekraftbackend.exceptions;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
    private final String identifier;

    public EntityAlreadyExistsException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }
}
