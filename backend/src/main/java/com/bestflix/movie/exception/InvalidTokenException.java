package com.bestflix.movie.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException{

    public InvalidTokenException(String token){
        super(new ErrorMessage(token, MessageType.NO_TOKEN_EXIST) , HttpStatus.BAD_REQUEST);
    }
}
