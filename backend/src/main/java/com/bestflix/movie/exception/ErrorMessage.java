package com.bestflix.movie.exception;

import lombok.*;


/**
 * Represents a structured error message used across the application.
 * Provides semantic details and categorization using {@link MessageType}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    /**
     * Additional context or description of the error.
     */
    private String details;

    /**
     * Type of message categorizing the error.
     */
    private MessageType messageType;

    /**
     * Returns a full error message by combining the {@link MessageType} message and optional details.
     *
     * @return composed error message string
     */
    public String getFullMessage() {
        return details != null
                ? messageType.getMessage() + ": " + details
                : messageType.getMessage();
    }

    /**
     * Returns the error code associated with the {@link MessageType}.
     *
     * @return application-specific error code
     */
    public String getErrorCode() {
        return messageType.getCode();
    }
}

