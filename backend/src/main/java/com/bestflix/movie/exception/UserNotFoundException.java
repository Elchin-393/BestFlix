package com.bestflix.movie.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String username){
        super(new ErrorMessage(username, MessageType.NO_RECORD_EXIST) , HttpStatus.NOT_FOUND);
    }
}
