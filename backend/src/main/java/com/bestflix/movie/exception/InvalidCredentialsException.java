package com.bestflix.movie.exception;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String username){
        super(new ErrorMessage(username, MessageType.BAD_CREDENTIALS) , HttpStatus.BAD_REQUEST);
    }
}
