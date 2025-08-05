package com.bestflix.movie.handler;

import com.bestflix.movie.exception.BaseException;
import com.bestflix.movie.exception.ErrorMessage;
import com.bestflix.movie.exception.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


/**
 * Handles exceptions globally across the application and converts them into structured API error responses.
 * This class captures various exception types and transforms them into {@link ApiError} format.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles custom application-specific exceptions extending {@link BaseException}.
     *
     * @param ex the exception instance
     * @param request the current web request context
     * @return structured API error response with appropriate HTTP status
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException ex, WebRequest request) {
        ErrorMessage errorMessage = ex.getErrorMessage();
        HttpStatus status = resolveHttpStatus(errorMessage.getMessageType());

        ApiError apiError = createApiError(errorMessage, request, status);
        return ResponseEntity.status(status).body(apiError);
    }


    /**
     * Handles bean validation failures such as field-level validation errors.
     *
     * @param ex the validation exception containing field errors
     * @param request the current web request
     * @return structured error response with 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorMessage errorMessage = new ErrorMessage(message, MessageType.OTHER_EXCEPTION);
        HttpStatus status = resolveHttpStatus(errorMessage.getMessageType());
        ApiError apiError = createApiError(errorMessage, request, status);


        return ResponseEntity.badRequest().body(apiError);
    }


    /**
     * Handles all uncaught exceptions, logging the error and returning a generic message.
     *
     * @param ex the thrown exception
     * @param request the current web request
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, WebRequest request) {

        log.error("Unhandled exception occurred", ex);

        ErrorMessage errorMessage = new ErrorMessage("An unexpected error occurred", MessageType.OTHER_EXCEPTION);
        ApiError apiError = createApiError(errorMessage, request, HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }


    /**
     * Builds an {@link ApiError} instance from error metadata and request context.
     *
     * @param errorMessage the domain-specific error message
     * @param request the triggering request
     * @param status the HTTP status to return
     * @return a structured {@link ApiError} object
     */
    private ApiError createApiError(ErrorMessage errorMessage, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .hostname(getLocalHostname())
                .errorCode(errorMessage.getErrorCode())
                .message(errorMessage.getFullMessage())
                .timestamp(LocalDateTime.now())
                .path(getRequestPath(request))
                .build();

        return ApiError.builder()
                .statusCode(status.value())
                .errorDetails(errorDetails)
                .build();
    }


    /**
     * Attempts to resolve the server’s hostname.
     *
     * @return hostname as a string, or a default if unable to resolve
     */
    private String getLocalHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

    /**
     * Extracts the request URI path from the {@link WebRequest}.
     *
     * @param request the web request context
     * @return URI string of the incoming request
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }


    /**
     * Maps domain-specific {@link MessageType} to appropriate {@link HttpStatus}.
     *
     * @param messageType application error categorization
     * @return corresponding HTTP status
     */
    private HttpStatus resolveHttpStatus(MessageType messageType) {
        return switch (messageType) {
            case NO_RECORD_EXIST -> HttpStatus.NOT_FOUND;
            case OTHER_EXCEPTION -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}