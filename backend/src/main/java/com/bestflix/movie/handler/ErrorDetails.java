package com.bestflix.movie.handler;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Detailed metadata for an API error.
 * Includes host information, error code, path, timestamp, and human-readable message.
 */
@Getter
@Builder
public class ErrorDetails {

    /**
     * Hostname of the server where the error occurred.
     */
    private String hostname;

    /**
     * Application-specific error code for identifying error type.
     */
    private String errorCode;

    /**
     * Path of the request that triggered the error.
     */
    private String path;

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * Descriptive message explaining the error.
     */
    private String message;
}
