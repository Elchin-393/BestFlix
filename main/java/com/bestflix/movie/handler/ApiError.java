package com.bestflix.movie.handler;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the structure of an API error response returned to the client.
 * Encapsulates status code and detailed error metadata.
 */
@Getter
@Builder
public class ApiError {

    /**
     * HTTP status code associated with the error.
     */
    private int statusCode;

    /**
     * Encapsulates metadata related to the error, including origin, message, and timestamp.
     */
    private ErrorDetails errorDetails;
}
