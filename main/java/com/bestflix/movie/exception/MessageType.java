package com.bestflix.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing categorized error types used in {@link ErrorMessage}.
 * Each type contains an application-specific code and human-readable message.
 */
@Getter
@AllArgsConstructor
public enum MessageType {

    /**
     * No user record exists in the system.
     */
    NO_RECORD_EXIST("100", "Not Found Such A User"),

    /**
     * No movie records were found for the request.
     */
    NO_MOVIES_EXIST("200", "Not Found Movies"),

    /**
     * Token was missing or not provided.
     */
    NO_TOKEN_EXIST("300", "Not Found Token"),

    /**
     * Token has expired and is no longer valid.
     */
    TOKEN_EXPIRED("301", "Token Expired"),

    /**
     * Failure occurred while attempting to send an email.
     */
    MAIL_EXCEPTION("600", "Mail couldn't be sent"),

    /**
     * Provided login credentials are invalid.
     */
    BAD_CREDENTIALS("600", "Username or Password is wrong"),

    /**
     * Unclassified or generic application error.
     */
    OTHER_EXCEPTION("900","Unknown Error");

    /**
     * Application-specific code representing the error type.
     */
    private String code;

    /**
     * Descriptive message for this error type.
     */
    private String message;
}

