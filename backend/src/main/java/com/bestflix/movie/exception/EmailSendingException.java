package com.bestflix.movie.exception;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;

public class EmailSendingException extends BaseException{

    public EmailSendingException(String message, MailException ex){
        super(new ErrorMessage(message, MessageType.MAIL_EXCEPTION) , HttpStatus.BAD_REQUEST);
    }
}
