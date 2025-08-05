package com.bestflix.movie.exception;


import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(String message) {
        super(new ErrorMessage(message, MessageType.TOKEN_EXPIRED), HttpStatus.BAD_REQUEST);
    }
}
